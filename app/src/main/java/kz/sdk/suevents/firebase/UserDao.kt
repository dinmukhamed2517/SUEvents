package kz.sdk.suevents.firebase


import com.google.firebase.auth.FirebaseAuth
import kz.sdk.suevents.models.User

class UserDao(
    private var firebaseAuth:FirebaseAuth
):FRDBWrapper<User>(){
    override fun getTableName(): String {
        return firebaseAuth?.currentUser?.uid.toString()
    }
    override fun getClassType(): Class<User> {
        return User::class.java
    }
}