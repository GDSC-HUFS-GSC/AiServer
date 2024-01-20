//export GOOGLE_APPLICATION_CREDENTIALS="/Users/mingeun/Vibecap/prototype01/credential/<다운받은 json 파일 이름>"
export GOOGLE_APPLICATION_CREDENTIALS="/Users/hwangdong-gyu/Desktop/2학년 2학기/tragle-410316-25a3719f57cb.json" // 서비스 계정 키가 포함된 JSON 파일의 경로를 환경변수 GOOGLE_APPLICATION_CREDENTIALS에 저장.

// dependency 추가
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud</groupId>
      <artifactId>libraries-bom</artifactId>
      <version>26.29.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-vision</artifactId>
  </dependency>

  //만약 Gradle을 사용한다면 implementation 'com.google.cloud:google-cloud-vision:3.31.0' 추가해야함
  //만약 SBT를 사용한다면 libraryDependencies += "com.google.cloud" % "google-cloud-vision" % "3.31.0" 추가해야함
//---------------------------------------------------------------------------------------------------------------------------------------------------- API 호출 코드
  // Imports the Google Cloud client library 
  // ImageAnnotatorClient 객체를 사용해 Google Cloud에 요청을 보내고 응답을 받음.
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QuickstartSample {
  public static void main(String... args) throws Exception {
    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

      // The path to the image file to annotate
      String fileName = "./resources/wakeupcat.jpg";

      // Reads the image file into memory
      Path path = Paths.get(fileName);
      byte[] data = Files.readAllBytes(path);
      ByteString imgBytes = ByteString.copyFrom(data);

      // Builds the image annotation request
      List<AnnotateImageRequest> requests = new ArrayList<>();
      Image img = Image.newBuilder().setContent(imgBytes).build();
      Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
      AnnotateImageRequest request =
          AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
      requests.add(request);

      // Performs label detection on the image file
      BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
      List<AnnotateImageResponse> responses = response.getResponsesList();

      for (AnnotateImageResponse res : responses) {
        if (res.hasError()) {
          System.out.format("Error: %s%n", res.getError().getMessage());
          return;
        }

        for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
          annotation
              .getAllFields()
              .forEach((k, v) -> System.out.format("%s : %s%n", k, v.toString()));
        }
      }
    }
  }
}
