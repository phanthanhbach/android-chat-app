package com.example.androidchatapp.models

import java.util.Date

class Message {
    var _id: String = ""
    var sender: User = User()
    var receiver: User = User()
    var message: String = ""
    var createdAt: String = ""
}