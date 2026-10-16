package battleship;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

public class BoardView extends JFrame {
    private static final int SIZE = 10;
    private final JButton[][] gridButtons = new JButton[SIZE][SIZE];
    private static final String[] ROWS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    // Instância única (Singleton) para reutilizar a mesma janela durante o jogo
    private static BoardView instance;

    public static BoardView getInstance() {
        if (instance == null) {
            instance = new BoardView();
        }
        return instance;
    }

    public BoardView() {
        FlatDarkLaf.setup();

        setTitle("Batalha Naval - Tabuleiro Gráfico");
        setSize(560, 560);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Grelha 11x11 (cabeçalhos A-J e 1-10 + grelha 10x10)
        JPanel boardPanel = new JPanel(new GridLayout(SIZE + 1, SIZE + 1, 3, 3));
        boardPanel.setBackground(new Color(25, 25, 25));

        // Canto superior esquerdo vazio
        boardPanel.add(new JLabel("", SwingConstants.CENTER));

        // Cabeçalhos das Colunas (1 a 10)
        for (int c = 1; c <= SIZE; c++) {
            JLabel lblCol = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            lblCol.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblCol.setForeground(new Color(180, 180, 180));
            boardPanel.add(lblCol);
        }

        // Construção da grelha de botões
        for (int r = 0; r < SIZE; r++) {
            JLabel lblRow = new JLabel(ROWS[r], SwingConstants.CENTER);
            lblRow.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblRow.setForeground(new Color(180, 180, 180));
            boardPanel.add(lblRow);

            for (int c = 0; c < SIZE; c++) {
                JButton cell = new JButton();
                cell.setEnabled(false); // Apenas para visualização
                cell.setFocusable(false);
                cell.setFont(new Font("SansSerif", Font.BOLD, 12));
                cell.setBackground(new Color(35, 45, 60)); // Água por explorar
                gridButtons[r][c] = cell;
                boardPanel.add(cell);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    /**
     * Atualiza todas as células com base na matriz do Game.java
     */
    public void updateFromMap(char[][] map) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                char symbol = map[r][c];
                JButton btn = gridButtons[r][c];

                // Identifica os símbolos desenhados na matriz map[][]
                if (symbol == '*' || symbol == 'X') {
                    // Tiro certeiro num navio
                    btn.setBackground(new Color(211, 47, 47)); // Vermelho
                    btn.setText("X");
                    btn.setForeground(Color.WHITE);
                } else if (symbol == 'o' || symbol == 'O' || symbol == '~') {
                    // Tiro na água
                    btn.setBackground(new Color(30, 136, 229)); // Azul
                    btn.setText("~");
                    btn.setForeground(Color.WHITE);
                } else if (symbol == '#' || symbol == 'N') {
                    // Navio visível
                    btn.setBackground(new Color(76, 175, 80)); // Verde
                    btn.setText("#");
                    btn.setForeground(Color.WHITE);
                } else if (symbol == '.') {
                    // Posição adjacente a navio
                    btn.setBackground(new Color(55, 71, 79)); // Cinzento escuro
                    btn.setText("");
                } else {
                    // Água por explorar (espaço vazio)
                    btn.setBackground(new Color(35, 45, 60)); // Azul escuro
                    btn.setText("");
                }
            }
        }
        if (!isVisible()) {
            setVisible(true);
        }
        repaint();
    }
}