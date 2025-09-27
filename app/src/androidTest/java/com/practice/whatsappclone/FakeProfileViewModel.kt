package com.practice.whatsappclone

class FakeProfileViewModel : ProfileViewModel(userRepo = FakeUserRepository()) {

    fun getFakeUser(userId: String): Map<String, Any?> {
        return (userRepo as FakeUserRepository).getUserById(userId)
    }
}
