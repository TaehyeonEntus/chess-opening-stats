### [[click here to get yout stats! www.chessopeningstat.com]](https://www.chessopeningstat.com)  
x0gusplaysgroove<< MY ACCOUNT

![Image](https://github.com/user-attachments/assets/cbe7005a-a975-455b-a8e9-2a7a16604c79)

## 🚀 Key Features (주요 기능)
- **Account Linking & Data Sync:** Link your chess platform accounts to automatically fetch your latest game data.
  - **계정 연동 및 데이터 동기화:** Chess.com 등 체스 플랫폼 계정을 연동하여 최근 게임 데이터를 자동으로 가져옵니다.
- **Opening Performance Analysis:** Detailed win, draw, and loss rates for each opening you've played.
  - **오프닝 성적 분석:** 각 오프닝별 승률, 무승부율, 패배율을 상세하게 확인할 수 있습니다.
- **Opening Search & Filtering:** Search for specific openings by ECO code or name, and filter by game count or performance.
  - **오프닝 검색 및 필터링:** ECO 코드나 이름으로 특정 오프닝을 검색하고, 판 수나 성적에 따라 필터링할 수 있습니다.
- **Board Search:** Place pieces on the board to search for openings and your statistics from that specific position.
  - **체스보드 검색:** 보드 위에 기물을 직접 배치하여 해당 포지션의 오프닝과 본인의 성적을 검색할 수 있습니다.
- **Multilingual Support:** Supports both Korean and English.
  - **다국어 지원:** 한국어와 영어를 지원합니다.

## 🛠 Tech Stack
### Backend
- **Framework:** [Spring Boot 3.4.1](https://spring.io/projects/spring-boot)
- **Language:** Java 21
- **Cache**: Caffeine Cache
- **API Docs:** Spring REST Docs
- **Build Tool:** Gradle
- **Deploy**: Railway PaaS
- **Monitor** Grafana, Prometheus
- **etc:**
- - **CSV Data Parser:** Apache Commons CSV
  - **Chess EPD Parser:** [chesslib](https://github.com/bhlangonijr/chesslib)

  
### Frontend
- **Framework:** [Next.js](https://nextjs.org/) (App Router), [React 19](https://react.dev/)
- **Language:** [TypeScript](https://www.typescriptlang.org/)
- **Styling:** [TailwindCSS](https://tailwindcss.com/), [Shadcn UI](https://ui.shadcn.com/)
- **Chess Logic & UI:** [react-chessboard](https://github.com/Clariity/react-chessboard), [chess.js](https://github.com/jhlywa/chess.js)
- **Deploy**: Railway PaaS
