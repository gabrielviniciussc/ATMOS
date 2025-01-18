# Atmos - Previsão do Tempo

O **Atmos** é um aplicativo de previsão do tempo que permite ao usuário buscar a previsão do clima para uma cidade específica. O aplicativo utiliza dados fornecidos pela API **OpenWeatherMap** para apresentar informações sobre a temperatura, condição climática, umidade e velocidade do vento.

## Estrutura do Projeto

Este projeto é composto por três componentes principais:

### 1. **AtmosAppGui**
A classe `AtmosAppGui` é a interface gráfica do usuário (GUI) do aplicativo. Ela é responsável por apresentar os dados de previsão do tempo para o usuário em uma janela gráfica.

- **Funções principais**:
  - Exibe uma janela com um campo de busca e um botão.
  - O usuário insere o nome de uma cidade no campo de busca e clica em "Buscar".
  - Após a consulta, os dados de clima são exibidos, incluindo a temperatura, condição climática, umidade e velocidade do vento.

### 2. **AtmosAppApi**
A classe `AtmosAppApi` é responsável por fazer as requisições à API do **OpenWeatherMap** para obter os dados de clima para a cidade solicitada. Ela utiliza o nome da cidade inserido pelo usuário para buscar as informações no serviço de previsão do tempo e retorna esses dados no formato JSON.

- **Funções principais**:
  - Faz uma requisição HTTP para o OpenWeatherMap usando a chave de API.
  - Processa a resposta JSON e extrai informações como a temperatura, condição climática, umidade e velocidade do vento.

### 3. **AppLauncher**
A classe `AppLauncher` é o ponto de entrada do aplicativo. Ela inicializa e exibe a interface gráfica do usuário (`AtmosAppGui`).

- **Funções principais**:
  - Cria uma instância da GUI (`AtmosAppGui`).
  - Exibe a interface para o usuário interagir.

## Como Testar o Aplicativo

### Requisitos
1. **Java 11 ou superior** deve estar instalado em seu sistema.
2. **Biblioteca JSON Simple**: o projeto depende da biblioteca `json-simple-1.1.1.jar` para processar os dados JSON da API.
3. **API Key do OpenWeatherMap**: Você precisará de uma chave de API do OpenWeatherMap para obter os dados climáticos. Se ainda não tem, você pode obter uma chave gratuita no site do [OpenWeatherMap](https://openweathermap.org/api).

### Passos para rodar o aplicativo

1. **Baixar e configurar o projeto**:
   - Baixe o código-fonte ou clone o repositório para o seu ambiente de desenvolvimento.
   - Adicione a chave da API OpenWeatherMap na classe `AtmosAppApi`. Substitua a variável `apiKey` pela sua chave pessoal:
   
     ```java
     private static final String API_KEY = "SUA_CHAVE_DE_API";
     ```

2. **Compilar e rodar**:
   - Compile o projeto utilizando o comando:

     ```bash
     javac -cp ".;json-simple-1.1.1.jar" AtmosAppGui.java AtmosAppApi.java AppLauncher.java
     ```

   - Execute o aplicativo com o comando:

     ```bash
     java -cp ".;json-simple-1.1.1.jar" AppLauncher
     ```

3. **Interação com o aplicativo**:
   - Ao iniciar o aplicativo, uma janela gráfica será exibida.
   - Digite o nome de uma cidade e clique no botão "Buscar".
   - A previsão do tempo será exibida, incluindo temperatura, condição climática, umidade e velocidade do vento.

### Exemplo de Teste
- **Cidade**: Tokyo
- **Resultado Esperado**: A janela exibirá informações como a temperatura de Tokyo, condição climática (por exemplo, "nuvens quebradas"), umidade e vento.

## Observações

- Certifique-se de que a sua chave de API do OpenWeatherMap está configurada corretamente.
- A API do OpenWeatherMap pode ter limitações de chamadas dependendo do seu plano (grátis ou pago).

---

Obrigado por usar o **Atmos**!
