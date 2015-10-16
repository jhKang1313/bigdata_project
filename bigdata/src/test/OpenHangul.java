package test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.StringTokenizer;

public class OpenHangul {
	public static void main(String[] args) {
		String sUrl = "http://api.openhangul.com/dic?api_key=chaos216920150814012144";

		String paramName = "q";

		long totstart = System.currentTimeMillis();
		HashMap<String, String> sensiWordH = new HashMap<>();

		int p = 0, n = 0;
		try {

			// http://api.openhangul.com/basic?q=보내고
			// http://api.openhangul.com/dic?api_key=jyChang20150803162326&q=좋다
			// chaos216920150814012144
			//FileInputStream fin = new FileInputStream("영화평\\" + args[0] + ".out");
			FileInputStream fin = new FileInputStream("2013.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(fin, "MS949"));

			FileInputStream fInput = new FileInputStream("감성단어.txt");
			BufferedReader sensiWordRd = new BufferedReader(new InputStreamReader(fInput,"UTF-8"));
			String line;

			// 감성단어.txt hashMap에 불러오기 
			while ((line = sensiWordRd.readLine()) != null) {
				String[] words = line.split("&");
				sensiWordH.put(words[0], words[1]);
			}

			sensiWordRd.close();
			fInput.close();


			BufferedWriter sensiWordWr = new BufferedWriter(new FileWriter("감성단어.txt",true));

			String revLine;
			int reviewNum = 1;
			while ((revLine = br.readLine()) != null) {   //통합리뷰파일 한줄씩 read
				StringTokenizer token = new StringTokenizer(revLine, " ");   //빈칸으로 나눔

				while (token.hasMoreTokens()) {
					String word = token.nextToken();
					BufferedReader inFile = null;
					try{

						URL url = new URL("http://api.openhangul.com/basic?q=" + word);   //단어 하나씩 url에 입력
						inFile = new BufferedReader(new InputStreamReader(url.openStream()));


						String originLine = null;
						int i = 1;
						while ((originLine = inFile.readLine()) != null) {   //단어 입력한 결과를 한줄씩 read

							if (i == 7) {                           //단어입력 후 결과 소스코드에서 7번째 line
								StringTokenizer token1 = new StringTokenizer(originLine, "\"");   //7번째 line을 "로 나눔
								int turn = 0;
								while (token1.hasMoreTokens()) {
									String originWord = token1.nextToken();   //"로 나눈 단어

									if (turn == 7) {                  //"로 나눈 단어 중 8번째 단어(원형으로 변환된 단어)
										if (originWord.equals(", ") == true)   //원형단어로 변경 실패되어 ,로 결과 출력되는 경우
											originWord = "error\n";            //원형단어를 error로 표현

										String value = sensiWordH.get(originWord);

										if(value!=null) {
											if(value.equals("긍정")) {
												p++;
												break;
											}
											else if(value.equals("부정")) {
												n++;
												break;
											}
											else if(value.equals("중립")) {
												break;
											}
										}

										else {
											URL url1 = new URL(sUrl + "&" + paramName + "=" + originWord);   //원형으로 변경된 단어를 감성분석에 입력
											InputStream a = url1.openStream();

											BufferedReader inFile1 = new BufferedReader(new InputStreamReader(a));

											String charLine = null;

											int i2 = 1;
											while ((charLine = inFile1.readLine()) != null) {   //감성분석한 결과 소스콛 한줄씩 read
												if (i2 == 8)                           //8번째 line에서 break;
													break;

												if (i2 == 7) {                           //7번째 line에서

													StringTokenizer token2 = new StringTokenizer(charLine, "\"");   //"로 구분

													int c = 0;
													int flag = 0;
													while (token2.hasMoreTokens()) {
														String charWord = token2.nextToken();
														if (c == 10 && charWord.equals(": ") == false) {   //원형 변환 실패시는 11번째 단어가 "긍정"/"부정" && 감성분석 실패하여 : 로 결과 출력되지 않은 경우

															if (charWord.equals("긍정")){                  //"긍정"일때
																p++;
																sensiWordWr.append(originWord+"&"+"긍정");
																sensiWordWr.flush();
																sensiWordWr.newLine();

															}
															else if (charWord.equals("부정")){            //"부정"일때
																n++;
																sensiWordWr.append(originWord+"&"+"부정");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															else if (charWord.equals("중립")){
																sensiWordWr.append(originWord+"&"+"중립");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															flag = 1;
														}
														if (c == 11 && flag == 0) {            // "로 구분하여 12번째 단어

															if (charWord.equals(", ") == true || charWord == null) // 감성분석 실패하여 , 로 표현시
																charWord = "error";

															if (charWord.equals("긍정")){
																p++;
																sensiWordWr.append(originWord+"&"+"긍정");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															else if (charWord.equals("부정")){
																n++;
																sensiWordWr.append(originWord+"&"+"부정");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															else if (charWord.equals("중립")){
																sensiWordWr.append(originWord+"&"+"중립");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}

															//                                             

														}
														if (c == 14 && flag == 1) {      //원형 변환 실패 했을 때 감성분석 정확도

															break;
														}
														if (c == 15 && flag == 0) {      //감성분석 정확도

															break;
														}
														c++;
													}

												}
												i2++;

											}
											inFile1.close();

											break;
										}
									}
									turn++;
								}
								break;
							}
							i++;
						}
					}
					catch(Exception e){
						System.out.println("error1");
						long totend = System.currentTimeMillis();
						System.out.println("경과시간 : " + (totend - totstart) / (1000.0) + "s");
						e.printStackTrace();
						break;
					}
					inFile.close();

				}
				reviewNum++;

			}
			br.close();
			sensiWordWr.close();
		} catch (Exception e) {
			System.out.println("error2");
			e.printStackTrace();
			long totend = System.currentTimeMillis();
			System.out.println("경과시간 : " + (totend - totstart) / (1000.0) + "s");
		}

		System.out.println(p+"\t"+n);

		try {
			FileOutputStream fos = new FileOutputStream("result.txt");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write("긍정\t부정");
			bw.newLine();
			bw.flush();
			bw.write(p+"\t"+n);
			bw.close();
			fos.close();

		} catch (Exception e) {
			System.out.println("error3");
			e.printStackTrace();
		}

		long totend = System.currentTimeMillis();
		System.out.println("총 경과시간 : " + (totend - totstart) / (1000.0) + "s");

	}

	public static void printByInputStream(InputStream is) {
		byte[] buf = new byte[1024];
		int len = -1;

		try {
			while ((len = is.read(buf, 0, buf.length)) != -1) {
				System.out.write(buf, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}