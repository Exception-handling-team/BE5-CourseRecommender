package app.standard;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Util {
    public static class File {

        public static void test() {
            System.out.println("파일 유틸 테스트");
        }

        public static void createFile(String pathValue) {

            write(pathValue, "");

        }

        public static String readAsString(String file) {

            Path filePath = Paths.get(file);

            try {
                return Files.readString(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        public static void write(String file, String content) {

            Path filePath = Paths.get(file);

            if(filePath.getParent() != null) {
                createDir(filePath.getParent().toString());
            }

            try {
                Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                System.out.println("파일 쓰기 실패");
                e.printStackTrace();
            }
        }

        public static void delete(String file) {
            Path filePath = Paths.get(file);

            try {
                Files.delete(filePath);
            } catch (IOException e) {
                System.out.println("파일 삭제 실패");
                e.printStackTrace();
            }
        }

        public static void createDir(String dirPath) {
            try {
                Files.createDirectories(Paths.get(dirPath));
            } catch (IOException e) {
                System.out.println("디렉토리 생성 실패");
                e.printStackTrace();
            }
        }

        public static void deleteForce(String path) {
            Path folderPath = Paths.get(path);
            if(!Files.exists(folderPath)) return;
            try {
                // 디렉토리 및 내용 삭제
                Files.walkFileTree(folderPath, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // 파일 삭제
                        Files.delete(file);
                        System.out.println("파일 삭제됨: " + file);
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        // 디렉토리 삭제 (내부 파일 모두 삭제 후 호출됨)
                        Files.delete(dir);
                        System.out.println("디렉토리 삭제됨: " + dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("폴더와 그 안의 내용이 성공적으로 삭제되었습니다.");
            } catch (IOException e) {
                System.err.println("폴더 삭제 중 오류 발생: " + e.getMessage());
            }
        }
    }
}
