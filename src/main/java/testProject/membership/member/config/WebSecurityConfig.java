package testProject.membership.member.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import testProject.membership.member.service.UserService;


@RequiredArgsConstructor
@EnableWebSecurity // Spring Security를 활성화
@Configuration
//WebSecurityConfigurerAdapter는 Spring Security의 설정파일로서의 역할
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService; // 유저 정보를 가져올 클래스

    @Override
    public void configure(WebSecurity web) {
        // 인증을 무시할 경로를 설정
        // (css,js,img는 무조건 접근 가능해야하기 때문에)
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
// WebSecurityConfigurerAdapter를 상속받으면 오버라이드 가능
        http
                // 접근에 대한 인증 설정 가능
                .authorizeRequests()
                .antMatchers("/login", "/signup", "/user")// 경로 설정과 권한 설정
                .permitAll() // 누구나 접근 허용
                .antMatchers("/").hasRole("USER") // USER, ADMIN만 접근 가능
                .antMatchers("/admin").hasRole("ADMIN") // ADMIN만 접근 가능
                .anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
                .and()

                // 로그인에 관한 설정
                .formLogin()
                .loginPage("/login") // 로그인 페이지 링크
                .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트 주소
                .and()

                // 로그아웃에 관한 설정
                .logout()
                .logoutSuccessUrl("/login") // 로그아웃 성공시 리다이렉트 주소
                .invalidateHttpSession(true) // 세샨 정보 삭제 여부
        ;
    }
    //로그인 할떄 필요한 정보를 가져오는 곳
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
        // 해당 서비스(userService)에서는 UserDetailsService를 implements해서
        // loadUserByUsername() 구현해야함 (서비스 참고)

    }
}