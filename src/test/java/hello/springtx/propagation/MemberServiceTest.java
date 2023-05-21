package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.UnexpectedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService @Transactional : off
     * memberRepository @Transactional : on
     * logRepository @Transactional : on
     */

    @Test
    void outerTxOff_success(){
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    @Test
    void outerTxOff_fail(){
        String username = "로그예외_outerTxOff_fail";

        assertThatThrownBy(()->memberService.joinV1(username))
                        .isInstanceOf(RuntimeException.class);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService @Transactional : on
     * memberRepository @Transactional : off
     * logRepository @Transactional : off
     */
    @Test
    void singleTx(){
        String username = "singleTx";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService @Transactional : on
     * memberRepository @Transactional : on
     * logRepository @Transactional : on
     */
    @Test
    void outerTxOn_success(){
        String username = "outerTxOn_success";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService @Transactional : on
     * memberRepository @Transactional : on
     * logRepository @Transactional : on Exception
     */

    @Test
    void outerTxOn_fail(){
        String username = "로그예외_outerTxOff_fail";

        assertThatThrownBy(()->memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // 모든 데이터 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }


    /**
     * memberService @Transactional : on
     * memberRepository @Transactional : on
     * logRepository @Transactional : on Exception
     */

    @Test
    void recoverException_fail(){
        String username = "로그예외_recoverException_fail";

        assertThatThrownBy(()->memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        // 모든 데이터 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }


    /**
     * memberService @Transactional : on
     * memberRepository @Transactional : on
     * logRepository @Transactional : on(REQUIRES_NEW) Exception
     */

    @Test
    void recoverException_success(){
        String username = "로그예외_recoverException_success";

        memberService.joinV2(username);

        // 모든 데이터 롤백
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }


}