require("dotenv").config();

const express = require("express");
const bodyParser = require("body-parser");
const admin = require("firebase-admin");

const app = express();
app.use(bodyParser.json());

// Initialize Firebase Admin once
if (!admin.apps.length) {
  try {
    admin.initializeApp({
      credential: admin.credential.cert({
        projectId: process.env.FIREBASE_PROJECT_ID,
        clientEmail: process.env.FIREBASE_CLIENT_EMAIL,
        privateKey: process.env.FIREBASE_PRIVATE_KEY.replace(/\\n/g, "\n"),
      }),
    });
    console.log("✅ Firebase Admin initialized");
  } catch (err) {
    console.error("❌ Firebase Admin init error:", err);
  }
}

// Existing notification endpoint for general notifications
app.post("/send-notification", async (req, res) => {
  try {
    const { token, title, body } = req.body;

    if (!token || !title || !body) {
      return res.status(400).json({
        success: false,
        error: "Missing token, title, or body in request",
      });
    }

    const message = {
      notification: { title, body },
      token,
    };

    await admin.messaging().send(message);
    res.status(200).json({ success: true });
  } catch (err) {
    console.error("❌ Error sending notification:", err);
    res.status(500).json({ success: false, error: err.message });
  }
});


// New endpoint for sending incoming call notifications with data payload
app.post("/send-call-notification", async (req, res) => {
  try {
    const { token, callId, callerName, callType } = req.body;

    if (!token || !callId || !callerName) {
      return res.status(400).json({
        success: false,
        error: "Missing token, callId, or callerName in request",
      });
    }

    const message = {
      token,
      data: {
        callId,
        callerName,
        callType: callType || "video", // default to video if undefined
      },
      android: {
        priority: "high",
        notification: {
          channelId: "calls_channel",
          title: "Incoming Call",
          body: `Call from ${callerName}`,
          sound: "default",
        },
      },
      apns: {
        payload: {
          aps: {
            sound: "default",
            category: "INCOMING_CALL",
          },
        },
      },
    };

    await admin.messaging().send(message);
    res.status(200).json({ success: true });
  } catch (err) {
    console.error("❌ Error sending call notification:", err);
    res.status(500).json({ success: false, error: err.message });
  }
});

// Local server listen (for local debugging, removed for Vercel production)
if (require.main === module) {
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => {
    console.log(`🚀 Server running on port ${PORT}`);
  });
}

module.exports = app;
