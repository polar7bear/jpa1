package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController // -> @Controller + @ResponseBody
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse createV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //v1의 방식대로 엔티티를 사용하지말고 dto를 사용하여 데이터를 바인딩 하자.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse createV2(@RequestBody @Valid RequestCreateMember request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateV2(@PathVariable("id") Long id, @RequestBody @Valid RequestUpdateMember request) {

        /*커맨드와 조회를 분리*/
        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);

        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    //조회를 할때 List에 엔티티를 전부 넘겨주게 되면 json object타입으로 들어가지 않고 배열 타입으로 반환되기 때문에
    //확장 가능성이 없고 유지보수에 불리함.
    //이를 해결하기 위해 마찬가지로 dto로 넘겨주면 된다.
    @GetMapping("/api/v1/members")
    public List<Member> findV1() {
        return memberService.findAll();
    }

    @GetMapping("/api/v2/members")
    public Result findV2() {
        List<Member> memberList = memberService.findAll();
        List<MemberDto> collect = memberList.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class RequestUpdateMember {
        private String name;
    }

    @Data
    static class RequestCreateMember {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}

