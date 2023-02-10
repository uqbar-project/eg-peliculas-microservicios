package org.uqbar.peliculamicroservicecontent.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("content")
class Content {
    @Id lateinit var id: String
    lateinit var file: String
}