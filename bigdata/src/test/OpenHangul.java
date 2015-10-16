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

			// http://api.openhangul.com/basic?q=������
			// http://api.openhangul.com/dic?api_key=jyChang20150803162326&q=����
			// chaos216920150814012144
			//FileInputStream fin = new FileInputStream("��ȭ��\\" + args[0] + ".out");
			FileInputStream fin = new FileInputStream("2013.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(fin, "MS949"));

			FileInputStream fInput = new FileInputStream("�����ܾ�.txt");
			BufferedReader sensiWordRd = new BufferedReader(new InputStreamReader(fInput,"UTF-8"));
			String line;

			// �����ܾ�.txt hashMap�� �ҷ����� 
			while ((line = sensiWordRd.readLine()) != null) {
				String[] words = line.split("&");
				sensiWordH.put(words[0], words[1]);
			}

			sensiWordRd.close();
			fInput.close();


			BufferedWriter sensiWordWr = new BufferedWriter(new FileWriter("�����ܾ�.txt",true));

			String revLine;
			int reviewNum = 1;
			while ((revLine = br.readLine()) != null) {   //���ո������� ���پ� read
				StringTokenizer token = new StringTokenizer(revLine, " ");   //��ĭ���� ����

				while (token.hasMoreTokens()) {
					String word = token.nextToken();
					BufferedReader inFile = null;
					try{

						URL url = new URL("http://api.openhangul.com/basic?q=" + word);   //�ܾ� �ϳ��� url�� �Է�
						inFile = new BufferedReader(new InputStreamReader(url.openStream()));


						String originLine = null;
						int i = 1;
						while ((originLine = inFile.readLine()) != null) {   //�ܾ� �Է��� ����� ���پ� read

							if (i == 7) {                           //�ܾ��Է� �� ��� �ҽ��ڵ忡�� 7��° line
								StringTokenizer token1 = new StringTokenizer(originLine, "\"");   //7��° line�� "�� ����
								int turn = 0;
								while (token1.hasMoreTokens()) {
									String originWord = token1.nextToken();   //"�� ���� �ܾ�

									if (turn == 7) {                  //"�� ���� �ܾ� �� 8��° �ܾ�(�������� ��ȯ�� �ܾ�)
										if (originWord.equals(", ") == true)   //�����ܾ�� ���� ���еǾ� ,�� ��� ��µǴ� ���
											originWord = "error\n";            //�����ܾ error�� ǥ��

										String value = sensiWordH.get(originWord);

										if(value!=null) {
											if(value.equals("����")) {
												p++;
												break;
											}
											else if(value.equals("����")) {
												n++;
												break;
											}
											else if(value.equals("�߸�")) {
												break;
											}
										}

										else {
											URL url1 = new URL(sUrl + "&" + paramName + "=" + originWord);   //�������� ����� �ܾ �����м��� �Է�
											InputStream a = url1.openStream();

											BufferedReader inFile1 = new BufferedReader(new InputStreamReader(a));

											String charLine = null;

											int i2 = 1;
											while ((charLine = inFile1.readLine()) != null) {   //�����м��� ��� �ҽ��� ���پ� read
												if (i2 == 8)                           //8��° line���� break;
													break;

												if (i2 == 7) {                           //7��° line����

													StringTokenizer token2 = new StringTokenizer(charLine, "\"");   //"�� ����

													int c = 0;
													int flag = 0;
													while (token2.hasMoreTokens()) {
														String charWord = token2.nextToken();
														if (c == 10 && charWord.equals(": ") == false) {   //���� ��ȯ ���нô� 11��° �ܾ "����"/"����" && �����м� �����Ͽ� : �� ��� ��µ��� ���� ���

															if (charWord.equals("����")){                  //"����"�϶�
																p++;
																sensiWordWr.append(originWord+"&"+"����");
																sensiWordWr.flush();
																sensiWordWr.newLine();

															}
															else if (charWord.equals("����")){            //"����"�϶�
																n++;
																sensiWordWr.append(originWord+"&"+"����");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															else if (charWord.equals("�߸�")){
																sensiWordWr.append(originWord+"&"+"�߸�");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															flag = 1;
														}
														if (c == 11 && flag == 0) {            // "�� �����Ͽ� 12��° �ܾ�

															if (charWord.equals(", ") == true || charWord == null) // �����м� �����Ͽ� , �� ǥ����
																charWord = "error";

															if (charWord.equals("����")){
																p++;
																sensiWordWr.append(originWord+"&"+"����");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															else if (charWord.equals("����")){
																n++;
																sensiWordWr.append(originWord+"&"+"����");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}
															else if (charWord.equals("�߸�")){
																sensiWordWr.append(originWord+"&"+"�߸�");
																sensiWordWr.flush();
																sensiWordWr.newLine();
															}

															//                                             

														}
														if (c == 14 && flag == 1) {      //���� ��ȯ ���� ���� �� �����м� ��Ȯ��

															break;
														}
														if (c == 15 && flag == 0) {      //�����м� ��Ȯ��

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
						System.out.println("����ð� : " + (totend - totstart) / (1000.0) + "s");
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
			System.out.println("����ð� : " + (totend - totstart) / (1000.0) + "s");
		}

		System.out.println(p+"\t"+n);

		try {
			FileOutputStream fos = new FileOutputStream("result.txt");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			bw.write("����\t����");
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
		System.out.println("�� ����ð� : " + (totend - totstart) / (1000.0) + "s");

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