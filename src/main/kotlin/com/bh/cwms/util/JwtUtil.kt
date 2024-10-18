package com.bh.cwms.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*
import java.util.function.Function


private const val jwtSecret = "Secret"
private const val expiration = 36000000L // EXPIRATION IN MILLIS ~ 10 Hours
private val signatureAlgorithm: SignatureAlgorithm = SignatureAlgorithm.HS256

/**
 * Function to extract all claims from a JWT
 *
 * @param token JWT Token
 */
fun extractAllClaims(
    token: String
) = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body

/**
 * Function to generate a new token for a username and user id.
 *
 * @param userId User ID to put in claim
 * @param username Username of user
 */
fun generateToken (username: String, userId: UUID) = Jwts.builder()
        .setClaims(mutableMapOf<String, Any>("userId" to userId))
        .setSubject(username)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + expiration))
        .signWith(signatureAlgorithm, jwtSecret).compact()

/**
 * Function to extract specific claim via a claim resolver.
 *
 * @param token JWT Token
 * @param claimResolver Claim Resolver
 */
fun <T> extractClaim(
    token: String,
    claimResolver: Function<Claims, T>
) = claimResolver.apply(extractAllClaims(token))


/**
 * Extract username from token
 *
 * @param token JWT Token
 *
 */
fun extractUsername(
    token: String
) = extractClaim(token, Claims::getSubject)

/**
 * Function to check for JWT expiration.
 *
 * @param token JWT Token
 */
fun isTokenExpired(
    token: String
) = extractClaim(token, Claims::getExpiration).before(Date())
