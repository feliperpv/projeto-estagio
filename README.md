# Projeto Bug

Configuração OpenCV - NetBeans - Windows

1º - Baixar OpenCV 3.0 através do link: https://sourceforge.net/projects/opencvlibrary/files/opencv-win/3.0.0/opencv-3.0.0.exe/download

2º - Colocar arquivo baixado no diretório C:

3º - Executar o arquivo e extrair dentro do C:\

Após clonar/baixar o projeto e abrir no NetBeans:

4º - Botão direito em cima do projeto, abrir o caminho "Propriedades"

5º - Na aba Biblioteca, clicar em "Adicionar JAR/Pasta". Procurar o caminho "C:\opencv\build\java" e selecionar o .jar

6º - Agora, na aba Execução em "Opções de VM" colocar -Djava.library.path="C:\opencv\build\java\x64", se for 64 bits e -Djava.library.path="C:\opencv\build\java\x86", se 32 bits.

7º - Projeto está configurado com a biblioteca do OpenCV
