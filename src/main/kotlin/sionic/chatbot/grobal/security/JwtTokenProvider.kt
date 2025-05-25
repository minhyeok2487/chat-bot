package sionic.chatbot.grobal.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretString: String,
    @Value("\${jwt.access-token-validity-in-seconds}") private val accessTokenValidityInSeconds: Long
) {
    private val logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString))

    companion object {
        private const val AUTHORITIES_KEY = "auth"
    }

    // 인증 객체를 기반으로 액세스 토큰 생성
    fun generateAccessToken(authentication: Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        val now = Date()
        val validity = Date(now.time + accessTokenValidityInSeconds * 1000)

        return Jwts.builder()
            .subject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key, Jwts.SIG.HS512)
            .compact()
    }

    // 토큰에서 인증 정보 조회
    fun getAuthentication(token: String): Authentication? {
        return try {
            val claims: Claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload

            val authorities: Collection<GrantedAuthority> =
                claims[AUTHORITIES_KEY].toString().split(",")
                    .filter { it.isNotEmpty() }
                    .map { SimpleGrantedAuthority(it) }
                    .toList()

            val principal: UserDetails = User(claims.subject, "", authorities)
            UsernamePasswordAuthenticationToken(principal, token, authorities)
        } catch (e: Exception) {
            logger.warn("JWT ERROR: ${e.message}")
            throw IllegalArgumentException("JWT ERROR: ${e.message}")
        }
    }

    // 토큰 유효성 검증
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            return true
        } catch (e: JwtException) {
            logger.info("Invalid JWT token: ${e.message}")
        } catch (e: IllegalArgumentException) {
            logger.info("JWT token compact of handler are invalid: ${e.message}")
        }
        return false
    }
}