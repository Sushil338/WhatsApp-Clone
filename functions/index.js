const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Firestore trigger: new message added to chats/{chatId}/messages/{messageId}
exports.sendMessageNotification = functions.firestore
  .document("chats/{chatId}/messages/{messageId}")
  .onCreate(async (snapshot, context) => {
    const messageData = snapshot.data();

    if (!messageData) {
      console.log("No message data found");
      return;
    }

    const receiverId = messageData.receiverId;
    const senderName = messageData.senderName || "New Message";
    const messageType = messageData.type || "text";

    // Get recipient user doc from Firestore
    const userDoc = await admin.firestore().collection("users").doc(receiverId).get();
    if (!userDoc.exists) {
      console.log(`User document for ID ${receiverId} not found`);
      return;
    }

    const tokens = userDoc.data()?.fcmTokens;
    if (!tokens || tokens.length === 0) {
      console.log(`No FCM tokens for user ${receiverId}`);
      return;
    }

    const payload = {
      notification: {
        title: senderName,
        body: messageType === "text" ? messageData.content : `Sent a ${messageType}`,
      },
      data: {
        chatId: context.params.chatId,
        senderId: messageData.senderId,
        messageId: context.params.messageId,
        type: messageType,
      },
    };

    try {
      const response = await admin.messaging().sendToDevice(tokens, payload);
      console.log("Notification sent successfully", response);
    } catch (error) {
      console.error("Error sending notification", error);
    }
  });
