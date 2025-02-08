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
            const contacts = user.contacts || [];
            
            contacts.sort(function (a, b) {
                const keyA = typeof a.updateAt === 'undefined' ? 0 : new Date(a.updateAt);
                const keyB = typeof b.updateAt === 'undefined' ? 0 : new Date(b.updateAt);

                if (keyA < keyB) return 1;
                if (keyA > keyB) return -1;

                return 0;
            })

            res.json({
                status: 'success',
                message: 'Chats fetched successfully',
                contacts: contacts
            });
        });

        app.use('/contacts', router);
    }
}