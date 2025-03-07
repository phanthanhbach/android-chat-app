const express = require("express");
const auth = require("./auth");
const ObjectID = require("mongodb").ObjectID;
const fileSystem = require("fs");

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
            const base64 = req.fields.base64 || "";
            const originalAttachmentName = req.fields.attachmentName || "";
            const extension = req.fields.extension || "";

            const currentDate = new Date();
            const createdAt = currentDate.toLocaleString("en-US", { timeZone: "Asia/Ho_Chi_Minh", hour12: false });

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

            let attachment = "";
            let attachmentName = "";

            if (base64 != "") {
                attachmentName = new Date().getTime() + "." + extension;
                attachment = "uploads/" + attachmentName;

                fileSystem.writeFile(attachment, base64, "base64", function (error) {
                    if (error) {
                        console.log(error);
                        return;
                    }
                });
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
                attachmentName: attachmentName,
                originalAttachmentName: originalAttachmentName,
                attachment: attachment,
                extension: extension,
                createdAt: createdAt
            }
            await db.collection("messages").insertOne(messageData);
            messageData.message = message;

            if (attachmentName != "") {
                messageData.attachment = mainURL + "/" + attachment;
            }

            await db.collection("users").findOneAndUpdate({
                $and: [{
                    _id: user._id
                }, {
                    "contacts.phone": receiver.phone
                }]
            }, {
                $set: {
                    "contacts.$.updateAt": new Date().toLocaleString("en-US", { timeZone: "Asia/Ho_Chi_Minh", hour12: false }),
                }
            })

            await db.collection("users").findOneAndUpdate({
                $and: [{
                    _id: receiver._id
                }, {
                    "contacts.phone": user.phone
                }]
            }, {
                $set: {
                    "contacts.$.hasUnreadMessage": 1,
                    "contacts.$.updateAt": new Date().toLocaleString("en-US", { timeZone: "Asia/Ho_Chi_Minh", hour12: false }),
                }
            })

            res.json({
                status: "success",
                message: "Message has been sent",
                messageData: messageData
            });
        });

        router.post("/fetch", auth, async function (req, res) {
            const user = req.user;
            const phone = req.fields.phone;
            if (!phone) {
                res.json({
                    status: "error",
                    message: "Missing required fields"
                });
                return;
            }

            const receiver = await db.collection("users").findOne({
                phone: phone
            });

            if (receiver == null) {
                res.json({
                    status: "error",
                    message: "Receiver not found"
                });
                return;
            }

            const messages = await db.collection("messages").find({
                $or: [{
                    "sender._id": user._id,
                    "receiver._id": receiver._id
                }, {
                    "sender._id": receiver._id,
                    "receiver._id": user._id
                }]
            }).sort({
                createAt: -1
            }).toArray();

            const data = [];
            // messages.forEach(message => {
            //     const decryptedText = crypto.AES.decrypt(message.message, key).toString(crypto.enc.Utf8);
            //     message.message = decryptedText;
            //     data.push(message);
            // });

            for (let i = 0; i < messages.length; i++) {
                let message = messages[i].message;
                let decryptedText = crypto.AES.decrypt(message, key).toString(crypto.enc.Utf8);

                const messageData = {
                    _id: messages[i]._id,
                    sender: messages[i].sender,
                    receiver: messages[i].receiver,
                    createdAt: messages[i].createdAt,
                    message: decryptedText,
                    attachmentName: (new Date().getTime()) + "." + messages[i].extension || "",
                    originalAttachmentName: messages[i].originalAttachmentName || "",
                    attachment: messages[i].attachment || "",
                    extension: messages[i].extension || ""
                }

                if (messageData.attachment != "") {
                    messageData.attachment = mainURL + "/" + messageData.attachment;
                }
                data.push(messageData);
            }
            data.reverse();

            await db.collection("users").findOneAndUpdate({
                $and: [{
                    _id: user._id
                }, {
                    "contacts.phone": phone
                }]
            }, {
                $set: {
                    "contacts.$.hasUnreadMessage": 0
                }
            })

            res.json({
                status: "success",
                message: "Data fetched successfully",
                data: data
            });
        });

        app.use("/chats", router);
    }
}