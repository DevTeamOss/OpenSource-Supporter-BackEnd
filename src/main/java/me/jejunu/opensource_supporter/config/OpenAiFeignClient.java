package me.jejunu.opensource_supporter.config;

import me.jejunu.opensource_supporter.dto.ChatGptRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "OpenAi", url = "https://api.openai.com")
public interface OpenAiFeignClient {
    @PostMapping(value = "/v1/chat/completions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    String getChatGpt(@RequestBody ChatGptRequestDto request, @RequestHeader("Authorization") String authorization);
}
