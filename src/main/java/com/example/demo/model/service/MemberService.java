package com.example.demo.model.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.domain.Member;
import com.example.demo.model.dto.MemberForm;
import com.example.demo.model.repository.MemberRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService implements UserDetailsService {

    private static final List<String> ROLES = List.of("USER", "MANAGER", "ADMIN");

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Member signup(MemberForm form) {
        validateSignupForm(form);

        if (memberRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Member member = new Member();
        member.setUsername(form.getUsername());
        member.setPassword(passwordEncoder.encode(form.getPassword()));
        member.setName(form.getName());
        member.setRole("USER");
        return memberRepository.save(member);
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다 : " + username));
    }

    public List<Member> findAll() {
        return memberRepository.findAll(Sort.by("id"));
    }

    public List<Member> findAll(String keyword) {
        if (isBlank(keyword)) {
            return findAll();
        }
        String search = keyword.strip();
        return memberRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(
            search, search, Sort.by("id"));
    }

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Member changeRole(Long id, String role) {
        if (role == null || !ROLES.contains(role)) {
            throw new IllegalArgumentException("허용되지 않는 권한입니다 : " + role);
        }

        Member member = findMember(id);
        preventSelfManagement(member);
        member.setRole(role);
        return memberRepository.save(member);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteMember(Long id) {
        Member member = findMember(id);
        preventSelfManagement(member);
        memberRepository.delete(member);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("회원 없음 : " + username));

        return User.builder()
            .username(member.getUsername())
            .password(member.getPassword())
            .roles(member.getRole())
            .build();
    }

    private void validateSignupForm(MemberForm form) {
        if (isBlank(form.getUsername())) {
            throw new IllegalArgumentException("아이디를 입력하세요.");
        }
        if (isBlank(form.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 입력하세요.");
        }
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (isBlank(form.getName())) {
            throw new IllegalArgumentException("이름을 입력하세요.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    private void preventSelfManagement(Member member) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (member.getUsername().equals(currentUsername)) {
            throw new IllegalArgumentException("자기 자신의 권한 변경·삭제는 할 수 없습니다.");
        }
    }
}
