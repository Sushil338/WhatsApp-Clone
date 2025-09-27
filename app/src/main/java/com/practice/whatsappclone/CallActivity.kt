package com.practice.whatsappclone

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import org.webrtc.*
import java.util.*

class CallActivity : ComponentActivity() {

    companion object {
        private const val TAG = "CallActivity"
        private val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )

        private var eglBase: EglBase? = null
        private var peerConnectionFactory: PeerConnectionFactory? = null
        private var peerConnection: PeerConnection? = null
        private var localVideoView: SurfaceViewRenderer? = null
        private var remoteVideoView: SurfaceViewRenderer? = null
        private var localVideoTrack: VideoTrack? = null
        private var localAudioTrack: AudioTrack? = null
        private var videoCapturer: VideoCapturer? = null
        private var surfaceTextureHelper: SurfaceTextureHelper? = null
    }

    private val firestore = FirebaseFirestore.getInstance()
    private var callDocId: String? = null
    private var callDocRef: DocumentReference? = null
    private var callerCandidatesListener: ListenerRegistration? = null
    private var calleeCandidatesListener: ListenerRegistration? = null
    private var callDocListener: ListenerRegistration? = null
    private val callViewModel: CallViewModel by viewModels()
    private var isCaller = false
    private var isInCall by mutableStateOf(false)
    private var callStarted = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantedMap ->
        val cameraGranted = grantedMap[Manifest.permission.CAMERA] == true
        val audioGranted = grantedMap[Manifest.permission.RECORD_AUDIO] == true
        if (cameraGranted && audioGranted) {
            initializePeerConnectionFactory()
            startLocalVideoCapture()
            postPermissionsCallSetup()
        } else {
            Log.w(TAG, "Permissions not granted")
            // Optionally show some UI indicating permissions are required
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callDocId = intent.getStringExtra("callId") ?: UUID.randomUUID().toString()
        callDocRef = firestore.collection("calls").document(callDocId!!)
        isCaller = intent.getBooleanExtra("launchedFromChat", false)

        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))

        setContent {
            var isMuted by remember { mutableStateOf(false) }
            var isCameraOn by remember { mutableStateOf(true) }
            var callStatus by remember { mutableStateOf("Connecting...") }

            LaunchedEffect(callDocId) {
                callDocId?.let { id ->
                    callViewModel.observeCallStatus(id) { newStatus ->
                        runOnUiThread {
                            when (newStatus.lowercase()) {
                                "ringing" -> callStatus = "Ringing..."
                                "accepted" -> callStatus = "Connected"
                                "ended" -> finishCallAndExit()
                                "missed" -> callStatus = "Missed"
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    "Call Status: $callStatus",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        isMuted = !isMuted
                        localAudioTrack?.setEnabled(!isMuted)
                    }) { Text(if (isMuted) "Unmute" else "Mute") }
                    Button(onClick = {
                        isCameraOn = !isCameraOn
                        localVideoTrack?.setEnabled(isCameraOn)
                    }) { Text(if (isCameraOn) "Camera Off" else "Camera On") }
                    Button(onClick = { finishCallAndExit() }) { Text("End Call") }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    AndroidView(factory = { ctx ->
                        SurfaceViewRenderer(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(400, 300)
                            initOrCreateEglBase()
                            setZOrderMediaOverlay(true)
                            localVideoView = this
                        }
                    })

                    Spacer(modifier = Modifier.width(8.dp))

                    AndroidView(factory = { ctx ->
                        SurfaceViewRenderer(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            initOrCreateEglBase()
                            remoteVideoView = this
                        }
                    })
                }
            }
        }
    }

    private fun postPermissionsCallSetup() {
        if (callDocId != null && !isInCall && !callStarted) {
            if (isCaller) {
                createPeerConnection()
                createCall()
            } else {
                createPeerConnection()
                answerCall()
            }
            isInCall = true
            callStarted = true
        }
    }

    private fun finishCallAndExit() {
        callerCandidatesListener?.remove()
        calleeCandidatesListener?.remove()
        callDocListener?.remove()
        try {
            videoCapturer?.stopCapture()
        } catch (_: Exception) {}
        localVideoView?.release()
        remoteVideoView?.release()
        surfaceTextureHelper?.dispose()
        peerConnection?.close()
        peerConnection?.dispose()
        peerConnection = null
        peerConnectionFactory?.dispose()
        peerConnectionFactory = null
        eglBase?.release()
        eglBase = null
        callDocRef?.update("ended", true)
            ?.addOnSuccessListener { Log.d(TAG, "Call ended in Firestore") }
            ?.addOnFailureListener { e -> Log.w(TAG, "Failed to mark call ended", e) }
        callDocRef?.update("status", "ended")
            ?.addOnSuccessListener { Log.d(TAG, "Call status set to ended") }
        callViewModel.removeCallStatusListener()
        finish()
    }

    private fun SurfaceViewRenderer.initOrCreateEglBase(): EglBase.Context {
        if (eglBase == null) eglBase = EglBase.create()
        init(eglBase!!.eglBaseContext, null)
        return eglBase!!.eglBaseContext
    }

    private fun initializePeerConnectionFactory() {
        if (eglBase == null) eglBase = EglBase.create()
        val initOptions = PeerConnectionFactory.InitializationOptions.builder(this)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initOptions)
        val encoderFactory = DefaultVideoEncoderFactory(eglBase!!.eglBaseContext, true, true)
        val decoderFactory = DefaultVideoDecoderFactory(eglBase!!.eglBaseContext)
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    private fun startLocalVideoCapture() {
        videoCapturer = createCameraCapturer()
        if (videoCapturer == null) {
            Log.w(TAG, "No camera capturer available")
            return
        }
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase!!.eglBaseContext)
        val videoSource = peerConnectionFactory!!.createVideoSource(false)
        videoCapturer!!.initialize(surfaceTextureHelper, applicationContext, videoSource.capturerObserver)
        videoCapturer!!.startCapture(640, 480, 30)
        localVideoTrack = peerConnectionFactory!!.createVideoTrack("VIDEO_TRACK_ID", videoSource)
        val audioSource = peerConnectionFactory!!.createAudioSource(MediaConstraints())
        localAudioTrack = peerConnectionFactory!!.createAudioTrack("AUDIO_TRACK_ID", audioSource)
        localVideoView?.let { localVideoTrack?.addSink(it) }
    }

    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator = Camera2Enumerator(this)
        for (deviceName in enumerator.deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                enumerator.createCapturer(deviceName, null)?.let { return it }
            }
        }
        for (deviceName in enumerator.deviceNames) {
            enumerator.createCapturer(deviceName, null)?.let { return it }
        }
        return null
    }

    private fun createPeerConnection() {
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        peerConnection = peerConnectionFactory?.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(newState: PeerConnection.SignalingState) {}
            override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) {}
            override fun onIceConnectionReceivingChange(receiving: Boolean) {}
            override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) {}
            override fun onIceCandidate(candidate: IceCandidate) {
                val data = mapOf(
                    "sdpMid" to (candidate.sdpMid ?: ""),
                    "sdpMLineIndex" to candidate.sdpMLineIndex,
                    "candidate" to candidate.sdp
                )
                val collection = if (isCaller) "callerCandidates" else "calleeCandidates"
                callDocRef?.collection(collection)?.add(data)
            }
            override fun onIceCandidatesRemoved(candidates: Array<IceCandidate>) {
                Log.d(TAG, "onIceCandidatesRemoved: ${candidates.contentToString()}")
            }
            override fun onAddStream(stream: MediaStream) {}
            override fun onRemoveStream(stream: MediaStream) {}
            override fun onDataChannel(dc: DataChannel) {}
            override fun onRenegotiationNeeded() {}
            override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {
                val track = receiver?.track()
                if (track is VideoTrack) runOnUiThread { remoteVideoView?.let { track.addSink(it) } }
            }
            override fun onTrack(transceiver: RtpTransceiver?) {
                val track = transceiver?.receiver?.track()
                if (track is VideoTrack) runOnUiThread { remoteVideoView?.let { track.addSink(it) } }
            }
        })
        localVideoTrack?.let { peerConnection?.addTrack(it) }
        localAudioTrack?.let { peerConnection?.addTrack(it) }
    }

    private fun createCall() {
        try {
            val mediaConstraints = MediaConstraints()
            peerConnection?.createOffer(object : SdpObserver {
                override fun onCreateSuccess(desc: SessionDescription) {
                    peerConnection?.setLocalDescription(object : SdpObserver {
                        override fun onSetSuccess() {
                            val offerData = mapOf(
                                "type" to desc.type.canonicalForm(),
                                "sdp" to desc.description
                            )
                            val callData = mapOf(
                                "offer" to offerData,
                                "status" to "ringing"
                            )
                            callDocRef?.set(callData)
                                ?.addOnSuccessListener { Log.d(TAG, "Offer set and status 'ringing' updated") }
                                ?.addOnFailureListener { e -> Log.w(TAG, "Failed to set offer and status", e) }
                            listenForAnswer()
                            listenForRemoteIceCandidates("calleeCandidates")
                        }
                        override fun onSetFailure(err: String?) { Log.w(TAG, "setLocalDescription failed: $err") }
                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onCreateFailure(p0: String?) {}
                    }, desc)
                }
                override fun onSetSuccess() {}
                override fun onCreateFailure(error: String?) { Log.w(TAG, "createOffer onCreateFailure: $error") }
                override fun onSetFailure(p0: String?) { Log.w(TAG, "createOffer onSetFailure: $p0") }
            }, mediaConstraints)
        } catch (e: Exception) {
            Log.e(TAG, "createCall error", e)
        }
    }

    private fun answerCall() {
        callDocRef?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot != null && snapshot.exists()) {
                val offerData = snapshot.get("offer") as? Map<*, *>
                val offerSdp = offerData?.get("sdp") as? String
                if (offerSdp != null) {
                    val sessionDescription = SessionDescription(SessionDescription.Type.OFFER, offerSdp)
                    peerConnection?.setRemoteDescription(object : SdpObserver {
                        override fun onSetSuccess() {
                            callDocRef?.update("status", "accepted")
                                ?.addOnSuccessListener { Log.d(TAG, "Call status set to accepted") }
                                ?.addOnFailureListener { e -> Log.w(TAG, "Failed to update call status", e) }
                            createAnswer()
                        }
                        override fun onSetFailure(p0: String?) { Log.w(TAG, "setRemoteDescription failed: $p0") }
                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onCreateFailure(p0: String?) {}
                    }, sessionDescription)
                } else {
                    Log.w(TAG, "Offer SDP is null in call doc")
                }
            } else {
                Log.w(TAG, "Call doc not found for id $callDocId")
            }
        }?.addOnFailureListener { e -> Log.w(TAG, "Failed to get call doc", e) }
    }

    private fun createAnswer() {
        val mediaConstraints = MediaConstraints()
        peerConnection?.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription) {
                peerConnection?.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() {
                        val answerData = mapOf(
                            "type" to desc.type.canonicalForm(),
                            "sdp" to desc.description
                        )
                        callDocRef?.update("answer", answerData)
                            ?.addOnSuccessListener { Log.d(TAG, "Answer set in Firestore") }
                            ?.addOnFailureListener { e -> Log.w(TAG, "Failed to set answer", e) }
                        listenForRemoteIceCandidates("callerCandidates")
                    }
                    override fun onSetFailure(p0: String?) { Log.w(TAG, "setLocalDescription failed for answer: $p0") }
                    override fun onCreateSuccess(p0: SessionDescription?) {}
                    override fun onCreateFailure(p0: String?) {}
                }, desc)
            }
            override fun onSetSuccess() {}
            override fun onCreateFailure(p0: String?) { Log.w(TAG, "createAnswer onCreateFailure: $p0") }
            override fun onSetFailure(p0: String?) {}
        }, mediaConstraints)
    }

    private fun listenForAnswer() {
        if (callDocRef == null) return
        callDocListener = callDocRef!!.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "listenForAnswer error", error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val answerData = snapshot.get("answer") as? Map<*, *>
                val sdp = answerData?.get("sdp") as? String
                if (!sdp.isNullOrEmpty()) {
                    peerConnection?.setRemoteDescription(object : SdpObserver {
                        override fun onSetSuccess() { Log.d(TAG, "Remote answer set") }
                        override fun onSetFailure(p0: String?) { Log.w(TAG, "setRemoteDescription(answer) failed: $p0") }
                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onCreateFailure(p0: String?) {}
                    }, SessionDescription(SessionDescription.Type.ANSWER, sdp))
                }
            }
        }
    }

    private fun listenForRemoteIceCandidates(collectionName: String) {
        val docRef = callDocRef ?: return
        val listener = docRef.collection(collectionName).addSnapshotListener { snapshot, error ->
            if (error != null) { Log.w(TAG, "listenForRemoteIceCandidates error", error); return@addSnapshotListener }
            if (snapshot != null) {
                for (dc in snapshot.documentChanges) {
                    if (dc.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val data = dc.document.data
                        try {
                            val sdpMid = data["sdpMid"] as? String ?: ""
                            val sdpMLineIndex = when (val v = data["sdpMLineIndex"]) {
                                is Long -> v.toInt()
                                is Int -> v
                                is Double -> v.toInt()
                                else -> 0
                            }
                            val candidateStr = data["candidate"] as? String ?: ""
                            val candidate = IceCandidate(sdpMid, sdpMLineIndex, candidateStr)
                            peerConnection?.addIceCandidate(candidate)
                        } catch (e: Exception) { Log.w(TAG, "Failed to parse remote ICE candidate", e) }
                    }
                }
            }
        }

        if (collectionName == "callerCandidates") {
            callerCandidatesListener?.remove()
            callerCandidatesListener = listener
        } else {
            calleeCandidatesListener?.remove()
            calleeCandidatesListener = listener
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callViewModel.removeCallStatusListener()
        callerCandidatesListener?.remove()
        calleeCandidatesListener?.remove()
        callDocListener?.remove()
        try {
            videoCapturer?.stopCapture()
        } catch (_: Exception) {}
        localVideoView?.release()
        remoteVideoView?.release()
        surfaceTextureHelper?.dispose()
        peerConnection?.dispose()
        peerConnectionFactory?.dispose()
        peerConnectionFactory = null
        eglBase?.release()
        eglBase = null
    }
}
