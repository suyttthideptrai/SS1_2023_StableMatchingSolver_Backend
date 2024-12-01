# GAMETHEORY + STABLEMATCHING SOLVER BACKEND APPLICATION (JAVA)
## Ứng dụng backend phục vụ cho việc giải các bài toán về game theory và stable matching bằng Java sử dụng Springboot MVC và MOEA Framework

## Table of Contents

- [Installation (Cài đặt)](#installation)
- [Usage (Sử dụng)](#usage)
- [Contributing (Đóng góp)](#contributing)

## Installation (Cài đặt)

Linux: Follow the steps
Windows: Follow the steps via WSL2

### Requirements (Yêu cầu)
1. WSL2 (For Windows only)
2. Git 
3. OpenJDK 17
4. Maven 3.8.3+
5. Apache ANT 1.10.14+

### Steps (Bước thực hiện)
1. Clone the repository (Clone repository này)
```bash 
git clone https://github.com/suyttthideptrai/SS1_2023_StableMatchingSolver_Backend.git
```
2. Build Custom MOEA Dependency
```bash
sudo -u $(whoami) bash setup.sh
```
3. Build the project (Build project)
```bash
mvn clean package
```

## Usage (Sử dụng)
Chạy file jar sau khi build xong
```bash
java -jar target/SS2_Backend-0.0.1-SNAPSHOT.jar
```
hoặc chạy trực tiếp trên IDE (IntelliJ IDEA, Eclipse, Netbeans, ...) bằng cách chạy class `StableMatchingSolverApplication.java`

## Contributing (Đóng góp)

### For GitHub collaborators

1. Tạo branch mới (`git checkout -b extra-feature`)
2. Commit thay đổi của bạn (`git commit -m 'Add extra feature'`)
3. Push lên branch (`git push origin extra-feature`)
4. Tạo pull request vào nhánh default: master trên github.

### For non GitHub repository collaborators

1. Fork
2. Code
3. Tạo pull request vào nhánh default: master trên github.

### Mọi vấn đề khó khăn hoặc thắc mắc có thể liên hệ với các thành viên khóa trước, trợ giảng hoặc thầy cô hướng dẫn.

