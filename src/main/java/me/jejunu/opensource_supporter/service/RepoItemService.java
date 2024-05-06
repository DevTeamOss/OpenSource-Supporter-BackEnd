package me.jejunu.opensource_supporter.service;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.RepoItemCreateRequestDto;
import me.jejunu.opensource_supporter.repository.RepoItemRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepoItemService {
    private final GithubApiFeignClient githubApiFeignClient;
    private final RepoItemRepository repoItemRepository;
    private final UserRepository userRepository;
    public RepoItem createRepoItem(RepoItemCreateRequestDto request) {

        String access_token = request.getAccess_token();
        String userName = request.getUserName();
        String repoName = request.getRepoName();
        String description = request.getDescription();
        List<String> tags = request.getTags();
        String repositoryLink = "https://github.com/" + userName + "/" + repoName;
        //MostLanguage
        JSONObject mostLanguageResponse = new JSONObject(githubApiFeignClient.getMostLanguage(userName, repoName, access_token));
        String mostLanguage = findMostUsedLanguage(mostLanguageResponse);
        //License
        String license = findLicense(userName, repoName, access_token);
        //LastCommitAt
        JSONObject commitResponse = new JSONObject(githubApiFeignClient.getCommitSha(userName, repoName, access_token));
        LocalDateTime lastCommitAt = findLastCommitAt(commitResponse); //Github측에서 받아오도록 구성
        //userID
        User user = findUserId(userName);

        System.out.println("userId = " + user.getId());
        System.out.println("Access Token: " + access_token);
        System.out.println("User Name: " + userName);
        System.out.println("Repository Name: " + repoName);
        System.out.println("Description: " + description);
        System.out.println("Tags: " + tags);
        System.out.println("Repository Link: " + repositoryLink);
        System.out.println("mostLanguageJSON = " + mostLanguage);
        System.out.println("licenseResponse = " + license);
        System.out.println("lastCommitAt = " + lastCommitAt);

        RepoItem newRepoItem = RepoItem.builder()
                .user(user)
                .repoName(repoName)
                .description(description)
                .tags(tags)
                .repositoryLink(repositoryLink)
                .mostLanguage(mostLanguage)
                .license(license)
                .lastCommitAt(lastCommitAt)
                .build();

        if(isRepoItemExists(repoName, user)){
            throw new RuntimeException("레포 등록 중복 에러");
        }


        return repoItemRepository.save(newRepoItem);
    }

    public String findMostUsedLanguage(JSONObject mostLanguageJSON) {
        String mostUsedLanguage = null;
        long maxLines = 0;

        // Iterate through each language and its line count
        Iterator<String> keys = mostLanguageJSON.keys();
        while (keys.hasNext()) {
            String language = keys.next();
            long lines = mostLanguageJSON.getLong(language);

            // Check if this language has more lines than the current max
            if (lines > maxLines) {
                maxLines = lines;
                mostUsedLanguage = language;
            }
        }

        return mostUsedLanguage;
    }
    private String findLicense(String userName, String repoName, String access_token) {
        try {
            JSONObject licenseResponse = new JSONObject(githubApiFeignClient.getRepoLicense(userName, repoName, access_token));

            // "license" 키의 값을 추출합니다.
            JSONObject licenseObject = licenseResponse.getJSONObject("license");
            // "name" 키의 값을 추출하여 license 변수에 저장합니다.
            String license = licenseObject.getString("name");
            // license 변수를 리턴합니다.
            return license;
        } catch (Exception e) {
            // 예외가 발생하면 null을 반환합니다.
            e.printStackTrace(); // 예외 상황을 로깅합니다.
            return "none License";
        }
    }

    public LocalDateTime findLastCommitAt(JSONObject commitResponse) {
        LocalDateTime lastCommitAt = null;

        if (commitResponse != null && commitResponse.has("pushed_at")) {
            String pushedAt = commitResponse.getString("pushed_at");

            // ISO_OFFSET_DATE_TIME 포맷의 문자열을 LocalDateTime으로 변환
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(pushedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            lastCommitAt = zonedDateTime.toLocalDateTime();
        }

        return lastCommitAt;
    }

    public User findUserId(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));
    }

    private boolean isRepoItemExists(String repoName, User user) {
        Optional<RepoItem> existingRepoItem = repoItemRepository.findByRepoNameAndUser(repoName, user);
        return existingRepoItem.isPresent();
    }
}