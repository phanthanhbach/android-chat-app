package com.example.androidchatapp.models

class Message {
    var _id: String = ""
    var sender: User = User()
    var receiver: User = User()
    var message: String = ""
    var createdAt: Double = 0.0
}