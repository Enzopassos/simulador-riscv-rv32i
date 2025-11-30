import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SimulatorGUI extends JFrame {
    private final CPU cpu;
    private final Bus bus;
    private final Registers registers;
    
    private JTable registersTable;
    private JTextArea vramArea;
    private JLabel pcLabel;
    private DefaultTableModel tableModel;

    public SimulatorGUI(CPU cpu, Bus bus, Registers registers) {
        this.cpu = cpu;
        this.bus = bus;
        this.registers = registers;

        setTitle("Simulador RISC-V - Interface Gráfica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        String[] columnNames = {"Reg", "Nome", "Valor (Hex)", "Valor (Dec)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        registersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(registersTable);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        add(scrollPane, BorderLayout.WEST);
        
        initRegistersTable();

        JPanel centerPanel = new JPanel(new BorderLayout());
        
        vramArea = new JTextArea();
        vramArea.setEditable(false);
        vramArea.setFont(new Font("Monospaced", Font.BOLD, 20));
        vramArea.setBackground(Color.BLACK);
        vramArea.setForeground(Color.GREEN);
        vramArea.setText("VRAM VAZIA");
        centerPanel.add(new JScrollPane(vramArea), BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel();
        pcLabel = new JLabel("PC: 0x00000000");
        pcLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(pcLabel);
        centerPanel.add(infoPanel, BorderLayout.NORTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnStep = new JButton("Próximo Passo (Step)");
        JButton btnRun = new JButton("Executar Rápido (10 steps)");
        
        btnStep.addActionListener(e -> executeStep());
        btnRun.addActionListener(e -> {
            for(int i=0; i<10; i++) executeStep();
        });
        
        buttonPanel.add(btnStep);
        buttonPanel.add(btnRun);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void initRegistersTable() {
        String[] regNames = {
            "zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2",
            "s0/fp", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
            "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7",
            "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"
        };
        
        for (int i = 0; i < 32; i++) {
            tableModel.addRow(new Object[]{"x" + i, regNames[i], "0x00000000", "0"});
        }
    }

    private void executeStep() {
        cpu.step();
        updateUI();
    }

    private void updateUI() {
        pcLabel.setText("PC: 0x" + Integer.toHexString(cpu.getPC()));

        int[] regValues = registers.getAll();
        for (int i = 0; i < 32; i++) {
            tableModel.setValueAt(String.format("0x%08X", regValues[i]), i, 2);
            tableModel.setValueAt(String.valueOf(regValues[i]), i, 3);
        }

        String vramContent = bus.getVRAMString();
        if (vramContent.isEmpty()) {
            vramArea.setText("[VRAM Vazia]");
        } else {
            vramArea.setText(vramContent);
        }
    }
}