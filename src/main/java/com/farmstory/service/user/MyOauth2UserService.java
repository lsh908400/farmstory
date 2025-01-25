package com.farmstory.service.user;

import com.farmstory.config.MyUserDetails;
import com.farmstory.entity.user.UserAddressEntity;
import com.farmstory.entity.user.UserEntity;
import com.farmstory.entity.user.UserPointDetailEntity;
import com.farmstory.entity.user.UserPointEntity;
import com.farmstory.repository.user.UserAddressRepository;
import com.farmstory.repository.user.UserPointDetailRepository;
import com.farmstory.repository.user.UserPointRepository;
import com.farmstory.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class MyOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserPointRepository userPointRepository;
    private final UserPointDetailRepository userPointDetailRepository;
    private final UserAddressRepository userAddressRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {



//        log.info("client-name : " + )
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            log.info("loadUser...1 : " + userRequest);

            String accessToken = userRequest.getAccessToken().getTokenValue();
            log.info("loadUser...2 : " + accessToken);

            String provider = userRequest.getClientRegistration().getRegistrationId();
            log.info("loadUser...3 : " + provider);

            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.info("loadUser...4 : " + oAuth2User);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("loadUser...5 : " + attributes);
            String email = (String) attributes.get("email");
            String uid = email.split("@")[0];
            String name = attributes.get("name").toString();
            String role = "guest";
            Random random = new Random();
            int randomNumber = random.nextInt(100000);

            String nick = name + randomNumber;

            Optional<UserEntity> optUser = userRepository.findByUserId(uid);

            if(optUser.isPresent()) {
                UserEntity user = optUser.get();

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            } else {
                UserEntity user = UserEntity.builder()
                        .userId(uid)
                        .userEmail(email)
                        .userName(name)
                        .userRole(role)
                        .userNick(nick)
                        .build();

                userRepository.save(user);

                Long userIdx =user.getUserIdx();
                UserPointEntity userPointEntity = UserPointEntity.builder()
                        .userIdx(userIdx)
                        .userPoint(new BigDecimal(0))
                        .build();

                userPointRepository.save(userPointEntity);

                UserPointDetailEntity userPointDetailEntity = UserPointDetailEntity.builder()
                        .point(userPointEntity)
                        .savePoint(new BigDecimal(1000))
                        .usePoint(BigDecimal.ZERO)
                        .detail("회원가입 축하 포인트")
                        .build();

                userPointDetailRepository.save(userPointDetailEntity);

                Optional<UserPointEntity> optUserPoint = userPointRepository.findByUserIdx(userIdx);

                List<UserPointDetailEntity> userPointDetails = userPointDetailRepository.findAllByPoint(optUserPoint.get());

                BigDecimal total = userPointDetails.stream()
                        .map(detail -> detail.getSavePoint().subtract(detail.getUsePoint())) // savePoint에서 usePoint를 뺌
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // 결과를 모두 더함


                UserPointEntity newUserPoint = UserPointEntity.builder()
                        .userPoint(total)
                        .userIdx(userIdx)
                        .pointIdx(optUserPoint.get().getPointIdx())
                        .build();

                userPointRepository.save(newUserPoint);

                UserAddressEntity userAddressEntity = UserAddressEntity.builder()
                        .userIdx(userIdx)
                        .addr("")
                        .addrZone("")
                        .addrDetail("")
                        .build();

                userAddressRepository.save(userAddressEntity);

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            }
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            log.info("loadUser...1 : " + userRequest);

            String accessToken = userRequest.getAccessToken().getTokenValue();
            log.info("loadUser...2 : " + accessToken);

            String provider = userRequest.getClientRegistration().getRegistrationId();
            log.info("loadUser...3 : " + provider);

            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.info("loadUser...4 : " + oAuth2User);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("loadUser...5 : " + attributes);
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            String email = (String) response.get("email");
            String uid = email.split("@")[0];
            String name = (String) response.get("name");
            String role = "guest";
            Random random = new Random();
            int randomNumber = random.nextInt(100000);

            String nick = name + randomNumber;
            Optional<UserEntity> optUser = userRepository.findByUserId(uid);

            if(optUser.isPresent()) {
                UserEntity user = optUser.get();

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            } else {
                UserEntity user = UserEntity.builder()
                        .userId(uid)
                        .userEmail(email)
                        .userName(name)
                        .userRole(role)
                        .userNick(nick)
                        .build();

                userRepository.save(user);

                Long userIdx =user.getUserIdx();
                UserPointEntity userPointEntity = UserPointEntity.builder()
                        .userIdx(userIdx)
                        .userPoint(new BigDecimal(0))
                        .build();

                userPointRepository.save(userPointEntity);

                UserPointDetailEntity userPointDetailEntity = UserPointDetailEntity.builder()
                        .point(userPointEntity)
                        .savePoint(new BigDecimal(1000))
                        .usePoint(BigDecimal.ZERO)
                        .detail("회원가입 축하 포인트")
                        .build();

                userPointDetailRepository.save(userPointDetailEntity);

                Optional<UserPointEntity> optUserPoint = userPointRepository.findByUserIdx(userIdx);

                List<UserPointDetailEntity> userPointDetails = userPointDetailRepository.findAllByPoint(optUserPoint.get());

                BigDecimal total = userPointDetails.stream()
                        .map(detail -> detail.getSavePoint().subtract(detail.getUsePoint())) // savePoint에서 usePoint를 뺌
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // 결과를 모두 더함


                UserPointEntity newUserPoint = UserPointEntity.builder()
                        .userPoint(total)
                        .userIdx(userIdx)
                        .pointIdx(optUserPoint.get().getPointIdx())
                        .build();

                userPointRepository.save(newUserPoint);

                UserAddressEntity userAddressEntity = UserAddressEntity.builder()
                        .userIdx(userIdx)
                        .addr("")
                        .addrZone("")
                        .addrDetail("")
                        .build();

                userAddressRepository.save(userAddressEntity);


                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            }

        } else {
            log.info("loadUser...1 : " + userRequest);

            String accessToken = userRequest.getAccessToken().getTokenValue();
            log.info("loadUser...2 : " + accessToken);

            String provider = userRequest.getClientRegistration().getRegistrationId();
            log.info("loadUser...3 : " + provider);

            OAuth2User oAuth2User = super.loadUser(userRequest);
            log.info("loadUser...4 : " + oAuth2User);

            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("loadUser...5 : " + attributes);

            Long id = (Long) attributes.get("id");

            // 카카오 사용자 정보에서 properties 가져오기
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            String nickname = (String) properties.get("nickname");
            String uid = nickname+id;
            String role = "guest";
            Random random = new Random();
            int randomNumber = random.nextInt(100000);
            String email = nickname+"@kakao.com";

            Optional<UserEntity> optUser = userRepository.findByUserId(uid);


            if(optUser.isPresent()) {
                UserEntity user = optUser.get();

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            } else {
                UserEntity user = UserEntity.builder()
                        .userId(uid)
                        .userEmail(email)
                        .userName(id+"")
                        .userRole(role)
                        .userNick(uid)
                        .build();

                userRepository.save(user);

                Long userIdx =user.getUserIdx();
                UserPointEntity userPointEntity = UserPointEntity.builder()
                        .userIdx(userIdx)
                        .userPoint(new BigDecimal(0))
                        .build();

                userPointRepository.save(userPointEntity);

                UserPointDetailEntity userPointDetailEntity = UserPointDetailEntity.builder()
                        .point(userPointEntity)
                        .savePoint(new BigDecimal(1000))
                        .usePoint(BigDecimal.ZERO)
                        .detail("회원가입 축하 포인트")
                        .build();

                userPointDetailRepository.save(userPointDetailEntity);

                Optional<UserPointEntity> optUserPoint = userPointRepository.findByUserIdx(userIdx);

                List<UserPointDetailEntity> userPointDetails = userPointDetailRepository.findAllByPoint(optUserPoint.get());

                BigDecimal total = userPointDetails.stream()
                        .map(detail -> detail.getSavePoint().subtract(detail.getUsePoint())) // savePoint에서 usePoint를 뺌
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // 결과를 모두 더함


                UserPointEntity newUserPoint = UserPointEntity.builder()
                        .userPoint(total)
                        .userIdx(userIdx)
                        .pointIdx(optUserPoint.get().getPointIdx())
                        .build();

                userPointRepository.save(newUserPoint);

                UserAddressEntity userAddressEntity = UserAddressEntity.builder()
                        .userIdx(userIdx)
                        .addr("")
                        .addrZone("")
                        .addrDetail("")
                        .build();

                userAddressRepository.save(userAddressEntity);

                return MyUserDetails.builder()
                        .user(user)
                        .accessToken(accessToken)
                        .attributes(attributes)
                        .build();
            }

        }

    }
}