/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pertemuan_11;

/**
 *
 * @author Ridho
 */
import pertemuan_11.Ligasawah;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.engine.JRException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JFileChooser;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class KelasemenLigaSawah extends javax.swing.JFrame {

   private EntityManagerFactory emf;
    private EntityManager em;
    DefaultTableModel model;

    InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    BufferedReader input = new BufferedReader(inputStreamReader);

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(KelasemenLigaSawah.class.getName());

    /**
     * Creates new form NewJFrame
     */
    public KelasemenLigaSawah() {
        initComponents();
        setLocationRelativeTo(null);
        connect();
        TampilkanTabel();

        TabelKlasemen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = TabelKlasemen.getSelectedRow();

                // Set nilai langsung ke dialog update
                NomorPendaftaran1.setText(TabelKlasemen.getValueAt(row, 0).toString());
                Klub1.setText(TabelKlasemen.getValueAt(row, 1).toString());
                AsalKlub1.setText(TabelKlasemen.getValueAt(row, 2).toString());
                Point1.setText(TabelKlasemen.getValueAt(row, 3).toString());

                // Set nilai langsung ke dialog delete  
                NomorPendaftaran2.setText(TabelKlasemen.getValueAt(row, 0).toString());
                Klub2.setText(TabelKlasemen.getValueAt(row, 1).toString());
                AsalKlub2.setText(TabelKlasemen.getValueAt(row, 2).toString());
                Point2.setText(TabelKlasemen.getValueAt(row, 3).toString());
            }
        });
    }

    public void connect() {
        try {
            emf = Persistence.createEntityManagerFactory("pertemuan_11PU");
            em = emf.createEntityManager();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
        }
    }

    private void imporCsvKeDatabase(File file) {
        int successCount = 0;
        int skipCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                // Skip header jika ada
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(";");
                if (data.length == 4) {
                    try {
                        int nomor = Integer.parseInt(data[0].trim());
                        String klub = data[1].trim();

                        // CEK DUPLIKAT NOMOR
                        if (isNomorExists(nomor)) {
                            errorMessages.append("Nomor ").append(nomor).append(" sudah ada, data dilewati\n");
                            skipCount++;
                            continue;
                        }

                        // CEK DUPLIKAT KLUB
                        if (isKlubExists(klub)) {
                            errorMessages.append("Klub '").append(klub).append("' sudah ada, data dilewati\n");
                            skipCount++;
                            continue;
                        }

                        // BUAT ENTITY DAN SIMPAN DENGAN JPA
                        Ligasawah ligasawah = new Ligasawah();
                        ligasawah.setNomor(nomor);
                        ligasawah.setKlub(klub);
                        ligasawah.setKotaasal(data[2].trim());
                        ligasawah.setPointkemenangan(Integer.parseInt(data[3].trim()));

                        em.getTransaction().begin();
                        em.persist(ligasawah);
                        em.getTransaction().commit();

                        successCount++;

                    } catch (NumberFormatException e) {
                        errorMessages.append("Error parsing number pada baris: ").append(line).append("\n");
                        skipCount++;
                    } catch (Exception e) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        errorMessages.append("Gagal menyimpan data: ").append(line).append(" - ").append(e.getMessage()).append("\n");
                        skipCount++;
                    }
                }
            }

            // Tampilkan summary
            String message = "Import selesai!\n"
                    + "Berhasil: " + successCount + " data\n"
                    + "Dilewati: " + skipCount + " data";

            if (errorMessages.length() > 0) {
                message += "\n\nDetail error:\n" + errorMessages.toString();
            }

            JOptionPane.showMessageDialog(this, message);
            TampilkanTabel();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal impor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void uploadCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File CSV");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String fileName = file.getName();

            if (!fileName.toLowerCase().endsWith(".csv")) {
                JOptionPane.showMessageDialog(this,
                        "File yang dipilih bukan file CSV!\nSilakan pilih file dengan ekstensi .csv",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // TIDAK PERLU KONEKSI JDBC LAGI, SUDAH PAKAI JPA
                imporCsvKeDatabase(file);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal impor: " + e.getMessage());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        InsertDialog = new javax.swing.JDialog();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        NomorPendaftaran = new javax.swing.JTextField();
        Klub = new javax.swing.JTextField();
        AsalKlub = new javax.swing.JTextField();
        Point = new javax.swing.JTextField();
        Simpan = new javax.swing.JButton();
        BatalInsert = new javax.swing.JButton();
        UpdateDialog = new javax.swing.JDialog();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        NomorPendaftaran1 = new javax.swing.JTextField();
        Klub1 = new javax.swing.JTextField();
        AsalKlub1 = new javax.swing.JTextField();
        Point1 = new javax.swing.JTextField();
        SimpanUpdate = new javax.swing.JButton();
        BatalUpdate = new javax.swing.JButton();
        DeleteDialog = new javax.swing.JDialog();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        NomorPendaftaran2 = new javax.swing.JTextField();
        Klub2 = new javax.swing.JTextField();
        AsalKlub2 = new javax.swing.JTextField();
        Point2 = new javax.swing.JTextField();
        SimpanDelete = new javax.swing.JButton();
        BatalDelete = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnDelete = new javax.swing.JButton();
        btnCetak = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TabelKlasemen = new javax.swing.JTable();
        btnUpdate = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Keluar = new javax.swing.JButton();
        btnInsert1 = new javax.swing.JButton();
        Upload = new javax.swing.JButton();

        jLabel7.setText("Nomor Pendaftaran");

        jLabel8.setText("Klub");

        jLabel9.setText("Asal Klub");

        jLabel10.setText("Point");

        NomorPendaftaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NomorPendaftaranActionPerformed(evt);
            }
        });

        Klub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KlubActionPerformed(evt);
            }
        });

        Simpan.setText("Insert");
        Simpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SimpanActionPerformed(evt);
            }
        });

        BatalInsert.setText("Batal");
        BatalInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BatalInsertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout InsertDialogLayout = new javax.swing.GroupLayout(InsertDialog.getContentPane());
        InsertDialog.getContentPane().setLayout(InsertDialogLayout);
        InsertDialogLayout.setHorizontalGroup(
            InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InsertDialogLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, InsertDialogLayout.createSequentialGroup()
                        .addComponent(Simpan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BatalInsert))
                    .addComponent(NomorPendaftaran, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(Klub)
                    .addComponent(AsalKlub)
                    .addComponent(Point))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        InsertDialogLayout.setVerticalGroup(
            InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InsertDialogLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(NomorPendaftaran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(Klub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(AsalKlub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(Point, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(InsertDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Simpan)
                    .addComponent(BatalInsert))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jLabel11.setText("Nomor Pendaftaran");

        jLabel12.setText("Klub");

        jLabel13.setText("Asal Klub");

        jLabel14.setText("Point");

        NomorPendaftaran1.setEditable(false);
        NomorPendaftaran1.setActionCommand("<Not Set>");
        NomorPendaftaran1.setEnabled(false);
        NomorPendaftaran1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NomorPendaftaran1ActionPerformed(evt);
            }
        });

        Klub1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Klub1ActionPerformed(evt);
            }
        });

        SimpanUpdate.setText("Update");
        SimpanUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SimpanUpdateActionPerformed(evt);
            }
        });

        BatalUpdate.setText("Batal");
        BatalUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BatalUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout UpdateDialogLayout = new javax.swing.GroupLayout(UpdateDialog.getContentPane());
        UpdateDialog.getContentPane().setLayout(UpdateDialogLayout);
        UpdateDialogLayout.setHorizontalGroup(
            UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateDialogLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(UpdateDialogLayout.createSequentialGroup()
                        .addComponent(SimpanUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addComponent(BatalUpdate))
                    .addComponent(NomorPendaftaran1)
                    .addComponent(Klub1)
                    .addComponent(AsalKlub1)
                    .addComponent(Point1))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        UpdateDialogLayout.setVerticalGroup(
            UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UpdateDialogLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(NomorPendaftaran1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(Klub1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(AsalKlub1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(Point1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(UpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SimpanUpdate)
                    .addComponent(BatalUpdate))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jLabel15.setText("Nomor Pendaftaran");

        jLabel16.setText("Klub");

        jLabel17.setText("Asal Klub");

        jLabel18.setText("Point");

        NomorPendaftaran2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NomorPendaftaran2ActionPerformed(evt);
            }
        });

        Klub2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Klub2ActionPerformed(evt);
            }
        });

        SimpanDelete.setText("Delete");
        SimpanDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SimpanDeleteActionPerformed(evt);
            }
        });

        BatalDelete.setText("Batal");
        BatalDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BatalDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DeleteDialogLayout = new javax.swing.GroupLayout(DeleteDialog.getContentPane());
        DeleteDialog.getContentPane().setLayout(DeleteDialogLayout);
        DeleteDialogLayout.setHorizontalGroup(
            DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeleteDialogLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(DeleteDialogLayout.createSequentialGroup()
                        .addComponent(SimpanDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                        .addComponent(BatalDelete))
                    .addComponent(NomorPendaftaran2)
                    .addComponent(Klub2)
                    .addComponent(AsalKlub2)
                    .addComponent(Point2))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        DeleteDialogLayout.setVerticalGroup(
            DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeleteDialogLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(NomorPendaftaran2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(Klub2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(AsalKlub2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(Point2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SimpanDelete)
                    .addComponent(BatalDelete))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 255, 102));

        jPanel1.setBackground(new java.awt.Color(153, 255, 153));

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnCetak.setText("Print");
        btnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakActionPerformed(evt);
            }
        });

        TabelKlasemen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nomor", "klub", "Asal", "Point"
            }
        ));
        TabelKlasemen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabelKlasemenMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TabelKlasemen);

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("STSong", 0, 48)); // NOI18N
        jLabel6.setText("KELASEMEN");

        jLabel5.setFont(new java.awt.Font("STSong", 0, 36)); // NOI18N
        jLabel5.setText("LIGA SAWAH");

        Keluar.setText("Keluar");
        Keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KeluarActionPerformed(evt);
            }
        });

        btnInsert1.setText("Insert");
        btnInsert1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsert1ActionPerformed(evt);
            }
        });

        Upload.setText("Upload");
        Upload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(92, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(201, 201, 201))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Upload)
                            .addComponent(btnUpdate)
                            .addComponent(btnCetak)
                            .addComponent(btnDelete)
                            .addComponent(Keluar)
                            .addComponent(btnInsert1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(228, 228, 228))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Upload, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCetak, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnInsert1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(Keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void TampilkanTabel() {
        try {
            TypedQuery<Ligasawah> query = em.createQuery(
                    "SELECT l FROM Ligasawah l ORDER BY l.pointkemenangan DESC, l.klub ASC",
                    Ligasawah.class
            );
            List<Ligasawah> ligasawahList = query.getResultList();

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Nomor");
            model.addColumn("Klub");
            model.addColumn("KotaAsal");
            model.addColumn("PointKemenangan");

            for (Ligasawah ligasawah : ligasawahList) {
                model.addRow(new Object[]{
                    ligasawah.getNomor(),
                    ligasawah.getKlub(),
                    ligasawah.getKotaasal(),
                    ligasawah.getPointkemenangan()
                });
            }
            this.TabelKlasemen.setModel(model);

        } catch (Exception ex) {
            Logger.getLogger(KelasemenLigaSawah.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error menampilkan data: " + ex.getMessage());
        }
    }
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed

        // TODO add your handling code here:
        UpdateDialog.pack();
        UpdateDialog.setLocationRelativeTo(this);
        UpdateDialog.setModal(true);
        UpdateDialog.setVisible(true);
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
//Delete Data
        int row = TabelKlasemen.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dulu data yang mau dihapus!");
            return;
        }

        DeleteDialog.pack();
        DeleteDialog.setLocationRelativeTo(this);
        DeleteDialog.setModal(true);
        DeleteDialog.setVisible(true);

    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakActionPerformed
         try {
            // Gunakan JDBC connection terpisah untuk reporting
            Class.forName("org.postgresql.Driver");
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/TugasPBO",
                    "postgres",
                    "er021105"
            );

            String path = ".\\src\\pertemuan_11\\reportKlasemen.jasper";
            JasperReport reports = (JasperReport) JRLoader.loadObjectFromFile(path);

            java.util.Map<String, Object> parameters = new java.util.HashMap<>();
            JasperPrint jprint = JasperFillManager.fillReport(reports, parameters, conn);

            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);

            conn.close();

        } catch (Exception ex) {
            Logger.getLogger(KelasemenLigaSawah.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "Terjadi kesalahan saat mencetak laporan:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCetakActionPerformed

    private void KeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KeluarActionPerformed

        int jawab = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (jawab == JOptionPane.YES_OPTION) {
            System.exit(0);
        }

    }//GEN-LAST:event_KeluarActionPerformed

    private void TabelKlasemenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabelKlasemenMouseClicked

        int baris = TabelKlasemen.getSelectedRow(); // ambil baris yang diklik
        if (baris != -1) {
            NomorPendaftaran.setText(TabelKlasemen.getValueAt(baris, 0).toString());
            Klub.setText(TabelKlasemen.getValueAt(baris, 1).toString());
            AsalKlub.setText(TabelKlasemen.getValueAt(baris, 2).toString());
            Point.setText(TabelKlasemen.getValueAt(baris, 3).toString());

            NomorPendaftaran1.setText(TabelKlasemen.getValueAt(baris, 0).toString());
            Klub1.setText(TabelKlasemen.getValueAt(baris, 1).toString());
            AsalKlub1.setText(TabelKlasemen.getValueAt(baris, 2).toString());
            Point1.setText(TabelKlasemen.getValueAt(baris, 3).toString());

            NomorPendaftaran2.setText(TabelKlasemen.getValueAt(baris, 0).toString());
            Klub2.setText(TabelKlasemen.getValueAt(baris, 1).toString());
            AsalKlub2.setText(TabelKlasemen.getValueAt(baris, 2).toString());
            Point2.setText(TabelKlasemen.getValueAt(baris, 3).toString());
        }

    }//GEN-LAST:event_TabelKlasemenMouseClicked

    private void NomorPendaftaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NomorPendaftaranActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NomorPendaftaranActionPerformed

    private void KlubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KlubActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KlubActionPerformed

    private void SimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SimpanActionPerformed
       try {
            if (NomorPendaftaran.getText().trim().isEmpty()
                    || Klub.getText().trim().isEmpty()
                    || AsalKlub.getText().trim().isEmpty()
                    || Point.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            int nomor = Integer.parseInt(NomorPendaftaran.getText());
            String klub = Klub.getText().trim();

            // Cek apakah nomor sudah ada
            if (isNomorExists(nomor)) {
                JOptionPane.showMessageDialog(this, "Nomor pendaftaran sudah ada! Gunakan nomor lain.");
                return;
            }

            // Cek apakah klub sudah ada
            if (isKlubExists(klub)) {
                JOptionPane.showMessageDialog(this, "Nama klub sudah ada! Gunakan nama klub lain.");
                return;
            }

            Ligasawah ligasawah = new Ligasawah();
            ligasawah.setNomor(nomor);
            ligasawah.setKlub(klub);
            ligasawah.setKotaasal(AsalKlub.getText().trim());
            ligasawah.setPointkemenangan(Integer.parseInt(Point.getText().trim()));

            em.getTransaction().begin();
            em.persist(ligasawah);
            em.getTransaction().commit();

            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            TampilkanTabel();
            resetInsertFields();
            InsertDialog.setVisible(false);

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            JOptionPane.showMessageDialog(this, "Gagal tambah data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_SimpanActionPerformed

    private void BatalInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BatalInsertActionPerformed
        // TODO add your handling code here:
        InsertDialog.setVisible(false);
    }//GEN-LAST:event_BatalInsertActionPerformed

    private void NomorPendaftaran1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NomorPendaftaran1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NomorPendaftaran1ActionPerformed

    private void Klub1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Klub1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Klub1ActionPerformed

    private void SimpanUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SimpanUpdateActionPerformed
         try {
            if (NomorPendaftaran1.getText().trim().isEmpty()
                    || Klub1.getText().trim().isEmpty()
                    || AsalKlub1.getText().trim().isEmpty()
                    || Point1.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            int newNomor = Integer.parseInt(NomorPendaftaran1.getText());
            String newKlub = Klub1.getText().trim();

            // Ambil nomor lama dari field (sebelum diubah)
            int oldNomor = Integer.parseInt(NomorPendaftaran1.getText());

            // Jika nomor diubah, cek apakah nomor baru sudah ada
            if (newNomor != oldNomor && isNomorExists(newNomor)) {
                JOptionPane.showMessageDialog(this, "Nomor pendaftaran sudah ada! Gunakan nomor lain.");
                return;
            }

            // Cek apakah klub baru sudah digunakan oleh klub lain
            Ligasawah existingByKlub = findKlubExists(newKlub);
            if (existingByKlub != null && existingByKlub.getNomor() != oldNomor) {
                JOptionPane.showMessageDialog(this, "Nama klub sudah ada! Gunakan nama klub lain.");
                return;
            }

            // 🔥 BAGIAN YANG HILANG - PROSES UPDATE:
            // Cari entity yang akan diupdate
            Ligasawah ligasawah = em.find(Ligasawah.class, oldNomor);
            if (ligasawah != null) {
                ligasawah.setNomor(newNomor);
                ligasawah.setKlub(newKlub);
                ligasawah.setKotaasal(AsalKlub1.getText().trim());
                ligasawah.setPointkemenangan(Integer.parseInt(Point1.getText().trim()));

                em.getTransaction().begin();
                em.merge(ligasawah);
                em.getTransaction().commit();

                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
                TampilkanTabel();
                resetUpdateFields();
                UpdateDialog.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!");
            }

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            JOptionPane.showMessageDialog(this, "Gagal update data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_SimpanUpdateActionPerformed

    private void BatalUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BatalUpdateActionPerformed
        // TODO add your handling code here:
        UpdateDialog.setVisible(false);
    }//GEN-LAST:event_BatalUpdateActionPerformed

    private void NomorPendaftaran2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NomorPendaftaran2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NomorPendaftaran2ActionPerformed

    private void Klub2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Klub2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Klub2ActionPerformed

    private void SimpanDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SimpanDeleteActionPerformed
       try {
            int confirmation = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin menghapus data dengan nomor: " + NomorPendaftaran2.getText() + "?",
                    "Konfirmasi Hapus Data",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            int nomor = Integer.parseInt(NomorPendaftaran2.getText());

            Ligasawah ligasawah = em.find(Ligasawah.class, nomor);
            if (ligasawah != null) {
                em.getTransaction().begin();
                em.remove(ligasawah);
                em.getTransaction().commit();

                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                TampilkanTabel();
                resetDeleteFields();
                DeleteDialog.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!");
            }

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            JOptionPane.showMessageDialog(this, "Gagal hapus data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_SimpanDeleteActionPerformed

    private void BatalDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BatalDeleteActionPerformed
        // TODO add your handling code here:
        DeleteDialog.setVisible(false);
    }//GEN-LAST:event_BatalDeleteActionPerformed

    private void btnInsert1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert1ActionPerformed
        // TODO add your handling code here:
        InsertDialog.pack();
        InsertDialog.setLocationRelativeTo(this);
        InsertDialog.setModal(true);
        InsertDialog.setVisible(true);

    }//GEN-LAST:event_btnInsert1ActionPerformed

    private void UploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UploadActionPerformed
        // TODO add your handling code here:
        uploadCsv();
    }//GEN-LAST:event_UploadActionPerformed

    /**
     * @param args the command line arguments
     */
    // Method untuk reset field insert dialog
    private boolean isNomorExists(int nomor) {
        try {
            Ligasawah ligasawah = em.find(Ligasawah.class, nomor);
            return ligasawah != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void resetInsertFields() {
    NomorPendaftaran.setText("");
    Klub.setText("");
    AsalKlub.setText("");
    Point.setText("");
}

// Method untuk reset field update dialog
    private void resetUpdateFields() {
    NomorPendaftaran1.setText("");
    Klub1.setText("");
    AsalKlub1.setText("");
    Point1.setText("");
}

// Method untuk reset field delete dialog
    private void resetDeleteFields() {
    NomorPendaftaran2.setText("");
    Klub2.setText("");
    AsalKlub2.setText("");
    Point2.setText("");
}
    
    private Ligasawah findKlubExists(String klub) {
        try {
            TypedQuery<Ligasawah> query = em.createQuery(
                    "SELECT l FROM Ligasawah l WHERE LOWER(l.klub) = LOWER(:klub)",
                    Ligasawah.class
            );
            query.setParameter("klub", klub);
            List<Ligasawah> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String args[]) {
    /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
    try {
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
    } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
        logger.log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>

    /* Create and display the form */
    java.awt.EventQueue.invokeLater(() -> new KelasemenLigaSawah().setVisible(true));
}

    private boolean isKlubExists(String klub) {
        try {
            TypedQuery<Ligasawah> query = em.createQuery(
                    "SELECT l FROM Ligasawah l WHERE LOWER(l.klub) = LOWER(:klub)",
                    Ligasawah.class
            );
            query.setParameter("klub", klub);
            return query.getResultList().size() > 0;
        } catch (Exception e) {
            return false;
        }
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AsalKlub;
    private javax.swing.JTextField AsalKlub1;
    private javax.swing.JTextField AsalKlub2;
    private javax.swing.JButton BatalDelete;
    private javax.swing.JButton BatalInsert;
    private javax.swing.JButton BatalUpdate;
    private javax.swing.JDialog DeleteDialog;
    private javax.swing.JDialog InsertDialog;
    private javax.swing.JButton Keluar;
    private javax.swing.JTextField Klub;
    private javax.swing.JTextField Klub1;
    private javax.swing.JTextField Klub2;
    private javax.swing.JTextField NomorPendaftaran;
    private javax.swing.JTextField NomorPendaftaran1;
    private javax.swing.JTextField NomorPendaftaran2;
    private javax.swing.JTextField Point;
    private javax.swing.JTextField Point1;
    private javax.swing.JTextField Point2;
    private javax.swing.JButton Simpan;
    private javax.swing.JButton SimpanDelete;
    private javax.swing.JButton SimpanUpdate;
    private javax.swing.JTable TabelKlasemen;
    private javax.swing.JDialog UpdateDialog;
    private javax.swing.JButton Upload;
    private javax.swing.JButton btnCetak;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnInsert1;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
