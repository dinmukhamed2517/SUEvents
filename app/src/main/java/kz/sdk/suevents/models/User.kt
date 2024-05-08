package kz.sdk.suevents.models

data class User(
    var name: String? = null,
    var lastname: String?= null,
    var phone:String?= null,
    var pictureUrl: String? = null,
    var favorites: Map<String, Event> = emptyMap(),
    var isAdmin:Boolean = false,
)