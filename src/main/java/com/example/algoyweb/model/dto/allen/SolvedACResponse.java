package com.example.algoyweb.model.dto.allen;

import com.google.gson.annotations.SerializedName;
import java.util.List;

//데이터베이스와 상관없는 Json 파싱을 위한 클래스 구현입니다
//JSON 구조를 반영한 Java 클래스 생성
//items -> titles -> title 최종 추출하여 리스트 생성
public class SolvedACResponse {
    //문제수(전체 원소 수)
    @SerializedName("count")
    private int count;
    //문제 내용(현재 페이지의 원소 목록)
    @SerializedName("items")
    private List<Item> items;

    public int getCount() {
        return count;
    }

    public List<Item> getItems() {
        return items;
    }

    // Item 문제 내용안에 또다른 Json
    public static class Item {
        //문제 아이디(백준 문제 번호로, 문제마다 고유)
        @SerializedName("problemId")
        private int problemId;
        //한국어 문제 제목입니다. HTML 엔티티나 LaTeX 수식을 포함할 수 있습니다.
        @SerializedName("titleKo")
        private String titleKo;
        //언어별 문제 제목 목록입니다.
        @SerializedName("titles")
        private List<Title> titles;

        public String getTitleKo() {
            return titleKo;
        }

        public List<Title> getTitles() {
            return titles;
        }
    }
    //Titles 안에 또다른 Json
    public static class Title {
        //문제 제목이 작성된 언어입니다.
        @SerializedName("language")
        private String language;
        //문제 제목입니다. (***추출해야할 부분***)
        @SerializedName("title")
        private String title;

        public String getTitle() {
            return title;
        }

        public String getLanguage() {
            return language;
        }
    }
}
