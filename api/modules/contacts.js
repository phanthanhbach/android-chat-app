const express = require('express');
const auth = require('./auth');

module.exports = {
    init(app) {
        const router = express.Router();

        router.post('/save', auth, async function (req, res) {
            const user = req.user;
            const contacts = JSON.parse(req.fields.contacts);

            await db.collection('users').findOneAndUpdate({
                _id: user._id
            }, {
                $set: {
                    contacts: contacts
                }
            });
            res.json({
                status: 'success',
                message: 'Contacts saved successfully'
            });
        });

        router.post('/fetch', auth, async function (req, res) {
            const user = req.user;
            // const userData = await db.collection('users').findOne({
            //     _id: user._id
            // });

            res.json({
                status: 'success',
                message: 'Chats fetched successfully',
                contacts: user.contacts
            });
        });

        app.use('/contacts', router);
    }
}