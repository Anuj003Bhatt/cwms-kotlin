package com.bh.cwms.service.user

import com.bh.cwms.util.EncryptionUtil
import com.bh.cwms.model.dto.AddUser
import com.bh.cwms.model.dto.UserDto
import com.bh.cwms.model.entity.User
import com.bh.cwms.repository.UserRepository
import com.bh.cwms.util.generateToken
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

interface UserService {
    fun createUser(newUser: AddUser): UserDto
    fun authenticate(username: String, password: String): Map<String, String>
}

@Service
class UserServiceImpl (
    private val userRepository: UserRepository
) : UserService {
    companion object {
        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java);
    }

    override fun createUser(newUser: AddUser): UserDto {
        val existingUser = userRepository.findByUsername(newUser.username)
        if (existingUser.isPresent) {
            log.error("")
            throw RuntimeException("User with same username already exists")
        }
        return userRepository.save(
            User(
                id = UUID.randomUUID(),
                username = newUser.username,
                password = EncryptionUtil.saltEncrypt(newUser.password)
            )
        ).toDto()
    }

    override fun authenticate(username: String, password: String): Map<String, String> {
        val user = userRepository.findByUsername(username).orElseThrow{
            throw RuntimeException("No user found for the username: '${username}'")
        }
        if (!EncryptionUtil.verifyPassword(password, user.password)) {
            throw RuntimeException("Invalid Credentials")
        }
        return mapOf(
            "token" to generateToken(username, user.id)
        )
    }
}