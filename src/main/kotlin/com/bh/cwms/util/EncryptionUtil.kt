package com.bh.cwms.util

import com.bh.cwms.model.entity.SaltEncrypt
import com.fasterxml.jackson.core.JsonProcessingException
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.security.*
import java.security.interfaces.RSAPublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import java.util.stream.Stream
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtil {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val secret: String = "Secret"
    private val secureRandom: SecureRandom = SecureRandom()
    private val iterations: Int = 1000
    private val characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val algorithm = "PBKDF2WithHmacSHA1"
    private val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance(algorithm)
    private val saltLength = 30
    private val keyLength = 256

    /**
     * Function to prepare a SecretKeySpec in order to encypt data.
     *
     * @return SecretKeySpec Object
     */
    fun prepareSecretKey(secret: String): SecretKeySpec {
        var key: ByteArray = secret.toByteArray(StandardCharsets.UTF_8)
        val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = key.copyOf(16)
        return SecretKeySpec(key, "AES")
    }

    /**
     * Function to encrypt a string using symmetric encryption and the secret constant from this file.
     *
     * @param originalString String to be encrypted
     * @param secret Encrypt using the provided secret
     *
     * @return encrypted string
     */
    fun encrypt(originalString: String, secret: String): String {
        return try {
            val secretKey: SecretKeySpec = prepareSecretKey(secret)
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            Base64.getEncoder().encodeToString(cipher.doFinal(originalString.toByteArray(StandardCharsets.UTF_8)))
        } catch (e: Exception) {
            log.error("Unable to encrypt the input string. Error: {}", e.message)
            throw RuntimeException(e)
        }
    }

    /**
     * Function to decrypt a string using symmetric encryption
     * and the secret constant from this file.
     *
     * @param encryptedString String to be decrypted
     * @param secret Custom secret to use for decryption
     *
     * @return original string
     */
    fun decrypt(encryptedString: String, secret: String): String {
        return try {
            val secretKey: SecretKeySpec = prepareSecretKey(secret)
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            String(cipher.doFinal(Base64.getDecoder().decode(encryptedString)))
        } catch (e: Exception) {
            log.error("Could not decrypt provided string. Error: {}", e.message)
            throw RuntimeException(e)
        }
    }

    /**
     * Overloaded function to use the standard secret only.
     *
     * @param originalString String to be encrypted
     */
    fun encrypt(originalString: String) = encrypt(originalString, secret)

    /**
     * Overloaded function to use the standard secret only.
     *
     * @param encryptedString String to be decrypted
     */
    fun decrypt(encryptedString: String) = decrypt(encryptedString, secret)

    /**
     * This method generates a random salt using which the encryption of the input is done.
     * This is used for one-way encryption for highly sensitive data like passwords.
     *
     * @return The generated salt value
     */
    private fun generateSalt() = Stream.generate {
        characters[secureRandom.nextInt(
            characters.length
        )]
        }.limit(saltLength.toLong()).toString()

    /**
     * Function to generate the hash from the input string bytes
     * and a randomly generated salt bytes.
     *
     * @param input String to encrypt
     * @param salt Salt to use for encryption
     * @return Hashed byte array
     */
    private fun hash(input: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(input, salt, iterations, keyLength)
        Arrays.fill(input, Character.MIN_VALUE)
        return try {
            keyFactory.generateSecret(spec).encoded
        } catch (e: InvalidKeySpecException) {
            log.error("Error while generating the hash for input. Error: {}", e.message)
            throw java.lang.RuntimeException("Error while generating hash for input")
        } finally {
            spec.clearPassword()
        }
    }

    /**
     * Overloaded function to generate the hash
     * from the input string and a randomly generated salt.
     *
     * @param input String to encrypt
     * @param salt Salt to use for encryption
     * @return Hashed byte array
     */
    private fun hash(input: String, salt: String) = hash(input.toCharArray(), salt.toByteArray())

    /**
     * Function to generate the encrypted map of string and salt for the input string.
     *
     * @param input String to encrypt
     * @return Map of salt and encrypted string
     */
    fun saltEncrypt(input: String): SaltEncrypt {
        // get salt
        val salt = generateSalt()
        // generate hash & base 64 encode
        val encoded = Base64.getEncoder().encodeToString(hash(input, salt))
        // return salt and hash
        return SaltEncrypt(
            salt = salt,
            value = encoded
        )
    }

    /**
     * Function to verify salt encrypted value
     * for one way encryption using the stored salt.
     *
     * @param input Input string to verify
     * @param actual Salted input to verify against
     * @return boolean
     */
    fun verifyPassword(input: String, actual: SaltEncrypt): Boolean {
        if (input.isBlank()) {
            log.error("Password is blank/empty")
            throw IllegalArgumentException("Password verification initiated with empty password")
        }
        val encoded = Base64.getEncoder().encodeToString(hash(input, actual.salt))
        return actual.value == encoded
    }

    /**
     * Function to generate a pair of private and public key
     *
     * @return KeyPair
     */
    fun generateKeyPair(): KeyPair {
        return try {
            val kpg = KeyPairGenerator.getInstance("RSA")
            kpg.initialize(2048)
            kpg.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            throw java.lang.RuntimeException("Unable to generate key pair")
        }
    }

    /**
     * Function to verify the set of public and private key
     *
     */
    fun verifyKeyPair(publicKey: String, privateKey: String) : Boolean {
        try {
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                ObjectOutputStream(byteArrayOutputStream).use {
                    val challenge = byteArrayOutputStream.toByteArray()
                    val kf = KeyFactory.getInstance("RSA")
                    val signature = Signature.getInstance("SHA256WithRSA")
                    val keySpecPKCS8 =
                        PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(privateKey))
                    val key = kf.generatePrivate(keySpecPKCS8)
                    signature.initSign(key)
                    signature.update(challenge)
                    val signatureBytes = signature.sign()
                    val keySpecPub = X509EncodedKeySpec(Base64.getMimeDecoder().decode(publicKey))
                    val pubKey = kf.generatePublic(keySpecPub) as RSAPublicKey
                    signature.initVerify(pubKey)
                    signature.update(challenge)
                    if (!signature.verify(signatureBytes)) {
                        throw RuntimeException("Invalid/Corrupt Transfer request")
                    }
                    return true
                }
            }
        } catch (e: JsonProcessingException) {
            throw java.lang.RuntimeException("Unable to perform transaction")
        } catch (e: java.lang.Exception) {
            throw RuntimeException("Corrupt transaction")
        }

    }

}