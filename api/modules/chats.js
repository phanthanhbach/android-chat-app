const express = require("express");
const auth = require("./auth");
const ObjectID = require("mongodb").ObjectID;

const crypto = require("crypto-js");
const algorithm = "aes-256-cbc";
const key = "abcdefghijklmnopqrstuvwxyz123456";

module.exports = {
    init(app) {
        const router = express.Router();

        router.post("/send", auth, async function (req, res) {
            const user = req.user;
            const phone = req.fields.phone;
            const message = req.fields.message;
            if (!phone || !message) {
                res.json({
                    status: "error",
                    message: "Missing required fields"
                });
                return;
            }

            const encryptedText = crypto.AES.encrypt(message, key).toString();
            const receiver = await db.collection("users").findOne({
                phone: phone
            })

            if (receiver == null) {
                res.json({
                    status: "error",
                    message: "Receiver not found"
                });
                return;
            }

            const messageData = {
                _id: ObjectID(),
                sender: {
                    _id: user._id,
                    name: user.name,
                    phone: user.phone
                },
                receiver: {
                    _id: receiver._id,
                    name: receiver.name,
                    phone: receiver.phone
                },
                message: encryptedText,
                createdAt: new Date().getTime()
            }
            await db.collection("messages").insertOne(messageData);
            messageData.message = message;

            res.json({
                status: "success",
                message: "Message has been sent",
                messageData: messageData
            });
        });

        app.use("/chats", router);
    }
}