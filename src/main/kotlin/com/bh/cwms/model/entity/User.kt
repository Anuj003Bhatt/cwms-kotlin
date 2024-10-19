package com.bh.cwms.model.entity

import com.bh.cwms.model.dto.UserDto
import com.bh.cwms.model.entity.base.DtoBridge
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UuidGenerator
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "users")
data class User (
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @UuidGenerator
    var id: UUID,

    @Column(name = "username")
    var username: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "password", nullable = false)
    var password: SaltEncrypt

) : DtoBridge<UserDto> {

    override fun toDto() = UserDto(
        id = id,
        username = username
    )
}