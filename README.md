```
## .env 설정
.env.example 파일을 이용하여
개인별 .env를 생성하여 사용하시면 됩니다!

📂 Backend MSA Architecture
├── 🚪 API Gateway (8080)         - 통합 진입점
├── 👤 User Service (8081)        - 사용자 관리
├── ❓ Question Service (8082)     - 질문 관리  
├── 💬 Answer Service (8083)      - 답변 관리
├── 🤖 AI Feedback Service (8084) - AI 피드백
├── 📊 Statistic Service (8085)   - 통계 관리
├── 🔍 Eureka Server (8761)       - 서비스 디스커버리
└── 🗄️ Data Layer
    ├── Redis (6379)              - 캐시
    ├── User DB (3311)            - 사용자 데이터
    ├── Question DB (3307)        - 질문 데이터
    ├── Answer DB (3308)          - 답변 데이터
    ├── Statistic DB (3309)       - 통계 데이터
    └── AI Feedback DB (3310)     - AI 피드백 데이터
```
