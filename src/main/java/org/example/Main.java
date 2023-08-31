package org.example;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Main {

    static EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("jpa_lab_2");

    public static void main(String[] args) {

        Member member = createMember("memberA", "회원"); // 준영속 상태의 member 엔티티를 반환

        /**
         * 준영속 상태에서 시작
         * member 엔티티를 관리하는 영속성 컨텍스트가 더는 존재하지 않으므로 수정사항을 데이터베이스에 반영할 수 없다.
         */
        member.setUsername("회원명 변경");

        // 준영속 상태의 엔티티를 수정하기 위해서는 준영속 -> 영속 상태로 변경해야한다.
        mergeMember(member);

    }

    /**
     *  member 엔티티는 createMember() 메소드의 영속성 컨텍스트1에서 영속 상태였다가
     *  영속성 컨텍스트1이 종료되면서 준영속 상태가 되었다.
     *  따라서, 해당 메서드는 준영속 상태의 member 엔티티를 반환한다.
     */
    private static Member createMember(String id, String username) {
        /* 영속성 컨텍스트1 시작 */
        EntityManager em1 = emf.createEntityManager();
        EntityTransaction tx1 = em1.getTransaction();
        tx1.begin();

        Member member = new Member();
        member.setId(id);
        member.setUsername(username);

        em1.persist(member);

        tx1.commit();

        em1.close();    // 영속성 컨텍스트1 종료
                        // member 엔티는 준영속 상태가 된다.
        /* 영속성 컨텍스트1 종료 */
        return member;
    }

    /**
     * 준영속 상태의 member 엔티티를 영속성 컨텍스트2가 관리하는 영속 상태로 변경 (mergeMember)
     * * member 엔티티가 준영속 상태에서 영속 상태로 변경되는 것은 아니고,
     * mergerMember라는 새로운 영속 상태의 엔티티가 반환된다.
     */
    private static void mergeMember(Member member) {
        /* 영속성 컨텍스트2 시작 */
        EntityManager em2 = emf.createEntityManager();
        EntityTransaction tx2 = em2.getTransaction();

        tx2.begin();
        // merger를 실행하며, 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회한다.
        // 1차 캐시에 엔티티가 없으면 데이터베이스에서 엔티티를 조회하고 1차 캐시에 저장한다.
        // 조회한 영속 엔티티에 member 엔티티 값을 채워 넣는다.
        //
        Member mergeMember = em2.merge(member);
        // 이름이 회원1에서 회원명 변경 된게 동작하여 데이터베이스에 반영한다.
        tx2.commit();

        // 준영속 상태
        System.out.println("member = " + member.getUsername()); // 회원명 변경

        // 영속 상태
        System.out.println("mergeMember = " + mergeMember.getUsername()); // 회원명 변경

        System.out.println("em2 contains member = " + em2.contains(member)); // false

        System.out.println("em2 contains mergeMember = " + em2.contains(mergeMember)); // true

        em2.close();
        /* 영속성 컨텍스트2 종료 */
    }




}