package tampilan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import tampilan.Edit_user.ButtonEditor;
import fungsi.*;
import database.*;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodListener;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Manage_supp extends JFrame {

	private JPanel panelCari = new JPanel();
	private JPanel panelTable = new JPanel();
	private JPanel panelManage = new JPanel();
	private Container kontainer = new Container();
	public int rowx;
	public int columnx;
	private dbSupplier sp = new dbSupplier();
	private JLabel lblCari = new JLabel("Cari");
	String[] cari = { "ID Supplier", "Nama Supplier" };
	private JComboBox cboCari = new JComboBox(cari);
	private JTextField txtCari = new JTextField(15);
	private JButton btnCari = new JButton("Cari");
	private Connection koneksi;
	public DefaultTableModel dataModel = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int column) {
			if (column >= 4) {
				return true;
			} else {
				return false;
			}
		}

	};
	public JTable table = new JTable(dataModel);
	private JScrollPane scroll = new JScrollPane(table);
	public String namaSupp = "";
	public String idSupp = "";

	public Manage_supp(String title) {
		super(title);
		super.setLocationRelativeTo(null);
		super.setSize(600, 500);

		super.setResizable(false);
		Container kontainer = getContentPane();
		kontainer.setLayout(new FlowLayout());

		panelCari.setLayout(new GridBagLayout());
		panelTable.setLayout(new GridBagLayout());
		panelManage.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		panelCari.setBorder(BorderFactory.createTitledBorder("Cari Data"));

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		panelCari.add(lblCari, c);

		cboCari.setSelectedItem(2);
		c.gridx = 1;
		c.gridy = 0;
		panelCari.add(cboCari, c);

		c.gridx = 2;
		c.gridy = 0;
		panelCari.add(txtCari, c);

		c.gridx = 3;
		c.gridy = 0;
		panelCari.add(btnCari, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		scroll.setPreferredSize(new Dimension(500, 300));
		c.gridx = 0;
		c.gridy = 0;
		panelTable.add(scroll, c);

		dataModel.addColumn("ID");
		dataModel.addColumn("Nama Supplier");
		dataModel.addColumn("Alamat");
		dataModel.addColumn("No. Telp");
		dataModel.addColumn("Edit");
		dataModel.addColumn("Hapus");

		scroll.setPreferredSize(new Dimension(550, 350));

		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);

		scroll.setVisible(true);

		// Start Condition Button
		createTable();
		cariData();
		kontainer.add(panelCari);
		kontainer.add(panelTable);
		kontainer.add(panelManage);
	}

	public void createTable() {

		// tambah tombol edit
		table.getColumnModel().getColumn(4)
				.setCellRenderer(new ButtonRenderer());
		table.getColumnModel().getColumn(4)
				.setCellEditor(new ButtonEditor(new JCheckBox()));

		// tambah tombol hapus
		table.getColumnModel().getColumn(5)
				.setCellRenderer(new ButtonRenderer());
		table.getColumnModel().getColumn(5)
				.setCellEditor(new ButtonEditor(new JCheckBox()));

		Vector<Supplier> vSupplier = sp.viewAll();
		for (int i = 0; i < vSupplier.size(); i++) {
			dataModel.addRow(new Object[] { vSupplier.elementAt(i).getIdsupp(),
					vSupplier.elementAt(i).getNama(),
					vSupplier.elementAt(i).getAlamat(),
					vSupplier.elementAt(i).getTelp(), "Edit", "Hapus" });
		}
		table.setModel(dataModel);

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Manage_supp mg = new Manage_supp("Manage Supplier");
		mg.setVisible(true);
		mg.setLocationRelativeTo(null);
	}

	public void cariData() {
		btnCari.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int kategori = cboCari.getSelectedIndex();
				String nKategori = "";
				if (kategori == 0) {
					nKategori = "idSupp";
				} else if (kategori == 1) {
					nKategori = "namaSupp";
				}
				String by = txtCari.getText();
				sp.viewAllBy(by, nKategori, txtCari, table, dataModel);
			}
		});
	}

	public void cariDataText() {
		btnCari.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int kategori = cboCari.getSelectedIndex();
				String nKategori = "";
				if (kategori == 0) {
					nKategori = "idSupp";
				} else if (kategori == 1) {
					nKategori = "namaSupp";
				}
				String by = txtCari.getText();
				sp.viewAllBy(by, nKategori, txtCari, table, dataModel);
			}
		});
	}

	class ButtonRenderer extends JButton implements TableCellRenderer {

		public ButtonRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	class ButtonEditor extends DefaultCellEditor {
		protected JButton button;

		private String label;
		private boolean isDeleteRow = false;
		private boolean isPushed;

		public ButtonEditor(JCheckBox checkBox) {
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			if (isSelected) {
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			} else {
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
			// label = (value == null) ? "" : value.toString();
			idSupp = table.getValueAt(row, 0).toString();
			namaSupp = table.getValueAt(row, 1).toString();
			rowx = row;
			columnx = column;
			label = (value == null) ? "" : value.toString();
			button.setText(label);
			isPushed = true;

			return button;
		}

		public Object getCellEditorValue() {
			if (isPushed) {
				//
				//
				// JOptionPane.showMessageDialog(button, username + ": Ouch!");
				// System.out.println(label + ": Ouch!");
				showEdit(rowx, columnx);

			}
			isPushed = false;
			return new String(label);
		}

		public boolean stopCellEditing() {
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped() {

			super.fireEditingStopped();
			if (isDeleteRow) {
				DefaultTableModel tableModel = (DefaultTableModel) table
						.getModel();
				tableModel.removeRow(table.getSelectedRow());
				table.addNotify();
				// tableModel.fireTableDataChanged();
			}
			isDeleteRow = false;
		}

		public void showEdit(int row, int column) {

			if (column == 4) {
				Manage_supp mg = new Manage_supp("");
				Final_editSupp edit_final = new Final_editSupp(mg,
						"Edit Supplier" + namaSupp, idSupp,
						ModalityType.APPLICATION_MODAL);
				edit_final.setVisible(true);
				// edit_final.setLocationRelativeTo(null);

			} else if (column == 5) {
				int pilih = JOptionPane.showConfirmDialog(null,
						"Yakin ingin menghapus " + namaSupp + " ?",
						"Delete Supplier?", JOptionPane.YES_NO_OPTION);
				if (pilih == 0) {
					try {
						hapusData(idSupp);
						isDeleteRow = true;
						JOptionPane.showMessageDialog(null,
								"Supplier telah dihapus!", "Supplier Deleted!",
								JOptionPane.INFORMATION_MESSAGE);

						namaSupp = "";
					} catch (Exception e) {
						// updateTable();
					}
				}
			}
		}

		public void hapusData(String Supplier) {

			try {
				Class.forName(Config.DATABASE_DRIVER).newInstance();
				koneksi = DriverManager.getConnection(Config.URL,
						Config.username, Config.password);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}

			try {
				Statement stmt = koneksi.createStatement();
				ResultSet rs = stmt
						.executeQuery("delete from Supplier where idSupp ='"
								+ idSupp + "'");
				// JOptionPane.showMessageDialog(null, "Data Berhasil dihapus",
				// "Delete Succes!", JOptionPane.INFORMATION_MESSAGE);
				koneksi.close();
				rs.close();
				stmt.close();
				isPushed = true;
			}

			catch (SQLException z) {
				z.printStackTrace();
			}

		}

	}

	public class Final_editSupp extends JDialog {
		private Connection koneksi;
		private dbSupplier sp = new dbSupplier();
		Manage_supp mg = new Manage_supp("");
		private int row = mg.rowx;
		private String password;
		private JLabel lblID = new JLabel("ID Supplier :");
		private JLabel lblNo = new JLabel("No");

		private JLabel lblNama = new JLabel("Nama :");
		private JTextField txtNama = new JTextField(15);

		private JLabel lblAlamat = new JLabel("Alamat :");
		private JTextField txtAlamat = new JTextField();

		private JLabel lblTelp = new JLabel("No. Telp");
		private JTextField txtTelp = new JTextField();

		private JButton btnEdit = new JButton("Edit");
		private JButton btnBatal = new JButton("Batal");

		private JPanel panelEdit = new JPanel();
		Container kontainer = getContentPane();

		public Final_editSupp(Manage_supp mg, String title, String idSupp,
				ModalityType modal) {
			super(mg, title, modal);
			super.setSize(400, 270);
			// super.setVisible(true);
			super.setLocationRelativeTo(null);
			super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			panelEdit.setVisible(true);
			panelEdit.setLayout(new GridBagLayout());
			kontainer.setLayout(new FlowLayout());
			GridBagConstraints c = new GridBagConstraints();
			panelEdit.setBorder(BorderFactory.createTitledBorder("Edit"));

			isiData(idSupp);

			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(10, 10, 10, 10);
			c.gridx = 0;
			c.gridy = 0;
			panelEdit.add(lblID, c);

			c.gridx = 1;
			c.gridy = 0;
			panelEdit.add(lblNo, c);

			c.gridx = 0;
			c.gridy = 1;
			panelEdit.add(lblNama, c);

			c.gridx = 1;
			c.gridy = 1;
			panelEdit.add(txtNama, c);

			c.gridx = 0;
			c.gridy = 2;
			panelEdit.add(lblAlamat, c);

			c.gridx = 1;
			c.gridy = 2;
			panelEdit.add(txtAlamat, c);

			c.gridx = 0;
			c.gridy = 3;
			panelEdit.add(lblTelp, c);

			c.gridx = 1;
			c.gridy = 3;
			panelEdit.add(txtTelp, c);

			c.gridx = 0;
			c.gridy = 4;
			btnEdit.setPreferredSize(new Dimension(150, 25));
			panelEdit.add(btnEdit, c);

			c.gridx = 1;
			c.gridy = 4;
			panelEdit.add(btnBatal, c);

			kontainer.add(panelEdit);

			btnBatal();
			btnSimpan();

		}

		/*
		 * public static void main(String[] args) { // TODO Auto-generated
		 * method stub Final_edit f_edit = new Final_edit("Edit Supplier","");
		 * f_edit.setVisible(true); f_edit.setLocationRelativeTo(null);
		 * 
		 * }
		 */

		public void btnBatal() {
			btnBatal.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					dispose();
				}
			});
		}

		public void btnSimpan() {
			btnEdit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					sp.editSupp(lblNo, txtNama, txtAlamat, txtTelp, table, rowx);
				}
			});
		}

		public void isiData(String idSupp) {
			try {
				Class.forName(Config.DATABASE_DRIVER).newInstance();
				koneksi = DriverManager.getConnection(Config.URL,
						Config.username, Config.password);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}

			try {
				Statement stmt = koneksi.createStatement();
				ResultSet rs = stmt
						.executeQuery("select * from Supplier where idSupp ='"
								+ idSupp + "'");
				while (rs.next()) {
					lblNo.setText(rs.getString(1));
					txtNama.setText(rs.getString(2));
					txtAlamat.setText(rs.getString(3));
					txtTelp.setText(rs.getString(4));
				}
				koneksi.close();
				rs.close();
			} catch (SQLException x) {
				x.printStackTrace();
			}
		}

	}

}
