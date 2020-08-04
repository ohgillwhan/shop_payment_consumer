package kr.sooragenius.shop.member.service.infra;

import kr.sooragenius.shop.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
