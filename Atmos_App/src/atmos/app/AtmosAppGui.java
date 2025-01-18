package atmos.app;
import javax.swing.*;
import java.awt.*;
import org.json.simple.JSONObject;

public class AtmosAppGui {

    private JFrame frame; // Criando uma instância do JFrame

    // Construtor da classe que cria e configura a interface
    public AtmosAppGui() {
        // Criando a janela principal
        frame = new JFrame("ATMOS - Previsão do Tempo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        // Painel superior com barra de busca
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 230, 140));
        topPanel.setPreferredSize(new Dimension(900, 70));

        JLabel logo = new JLabel("ATMOS", SwingConstants.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 36));
        logo.setForeground(Color.DARK_GRAY);

        JTextField searchField = new JTextField("Buscar cidade");
        searchField.setPreferredSize(new Dimension(200, 40));
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton searchButton = new JButton("Buscar");
        searchButton.setPreferredSize(new Dimension(100, 40));

        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);
        topPanel.add(logo, BorderLayout.WEST);

        // Painel principal para exibir informações
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        mainPanel.setBackground(new Color(200, 220, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new GridLayout(6, 1));

        mainPanel.add(leftPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(rightPanel);

        // Ação do botão de busca
        searchButton.addActionListener(e -> {
            String city = searchField.getText().trim();
            if (!city.isEmpty()) {
                JSONObject weatherData = AtmosAppApi.getWeatherData(city);
                if (weatherData != null) {
                    try {
                        // Verificação de dados essenciais
                        String cityName = city;

                        // Atualização da temperatura para verificar o tipo de dado
                        Object tempObject = weatherData.get("temperature");
                        double temp = 0;

                        if (tempObject instanceof Long) {
                            temp = ((Long) tempObject).doubleValue();  // Convertendo de Long para Double
                        } else if (tempObject instanceof Double) {
                            temp = (Double) tempObject;  // Se já for Double, apenas converte
                        } else {
                            // Caso inesperado
                            System.out.println("Erro: Tipo inesperado para temperatura");
                            return;
                        }

                        // Outras variáveis
                        String weatherCondition = (String) weatherData.get("weather_condition");
                        long humidity = (long) weatherData.get("humidity");
                        double windspeed = (double) weatherData.get("windspeed");

                        // Atualização do painel esquerdo com dados da cidade
                        leftPanel.removeAll();
                        leftPanel.add(new JLabel(cityName, SwingConstants.CENTER), BorderLayout.NORTH);
                        leftPanel.add(new JLabel(temp + "°C", SwingConstants.CENTER), BorderLayout.CENTER);
                        leftPanel.add(new JLabel(weatherCondition, SwingConstants.CENTER), BorderLayout.SOUTH);

                        // Atualiza o painel central com previsões
                        centerPanel.removeAll();
                        centerPanel.add(new JLabel("Previsão detalhada em breve...", SwingConstants.CENTER), BorderLayout.CENTER);

                        // Atualiza o painel direito com detalhes
                        rightPanel.removeAll();
                        rightPanel.add(new JLabel("Detalhes do clima", SwingConstants.CENTER));
                        rightPanel.add(new JLabel("Umidade: " + humidity + "%"));
                        rightPanel.add(new JLabel("Vento: " + windspeed + " km/h"));

                        // Revalida e repinta o frame
                        frame.revalidate();
                        frame.repaint();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Erro ao processar dados da cidade!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Cidade não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, insira o nome de uma cidade.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Adiciona os painéis ao frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    // Método para tornar a janela visível
    public void display() {
        frame.setVisible(true);  // Chama o setVisible no frame, não na classe AtmosAppGui
    }

    public static void main(String[] args) {
        // Criar a instância de AtmosAppGui e exibir a janela
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AtmosAppGui appGui = new AtmosAppGui();
                appGui.display();  // Aqui chamamos o método display para exibir a janela
            }
        });
    }
}
