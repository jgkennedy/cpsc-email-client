package cu.cs.cpsc215.project3;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

interface Command {
	void execute();
}

interface IMediator {
	public void add();
	public void edit();
	public void delete();
	public void registerAdd(BtnAdd a);
	public void registerEdit(BtnEdit b);
	public void registerDelete(BtnDelete d);
}

class BtnAdd extends JButton implements Command {
	private static final long serialVersionUID = 1328622L;
	IMediator med;

	BtnAdd(ActionListener al, IMediator m) {
		super("Add Contact");
		addActionListener(al);
		med = m;
		med.registerAdd(this);
	}

	public void execute() {
		med.add();
	}

}

class BtnEdit extends JButton implements Command {
	private static final long serialVersionUID = 923784L;
	IMediator med;

	BtnEdit(ActionListener al, IMediator m) {
		super("Edit Contact");
		addActionListener(al);
		med = m;
		med.registerEdit(this);
	}

	public void execute() {
		med.edit();
	}

}

class BtnDelete extends JButton implements Command {
	private static final long serialVersionUID = 798123L;
	IMediator med;

	BtnDelete(ActionListener al, IMediator m) {
		super("Delete Contact");
		addActionListener(al);
		med = m;
		med.registerDelete(this);
	}

	public void execute() {
		med.delete();
	}

}

public class MainFrame extends JFrame implements ActionListener, IMediator {
	private static final long serialVersionUID = 1745232698701012553L;
	private static MainFrame instance = null;
	private DataStore ds;
	private JTable contactsTbl = new JTable(new ContactTableModel());
	
	public static MainFrame getInstance() {
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}

	private MainFrame() {
		super("Email Client 1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ds = DataStore.getInstance();
		ds.load();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		
		URL imgURL = getClass().getResource("/res/icon.png");
		ImageIcon img = new ImageIcon(imgURL);
		setIconImage(img.getImage());
		
		setSize(800, 600);
		setLocationByPlatform(true);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 3, 0, 0));
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton frameAddBtn = new BtnAdd(this, this);
		JButton frameEditBtn = new BtnEdit(this, this);
		JButton frameDeleteBtn = new BtnDelete(this, this);

		panel.add(frameAddBtn);
		panel.add(frameEditBtn);
		panel.add(frameDeleteBtn);
		
		JScrollPane myScrollPane = new JScrollPane(contactsTbl);
		getContentPane().add(myScrollPane, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmCompose = new JMenuItem("Compose");
		mntmCompose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DialogMediator.getInstance().getTransDlg().setVisible(true);
			}
		});
		mnFile.add(mntmCompose);

		JMenuItem mntmAddContact = new JMenuItem("Add Contact");
		mntmAddContact.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainFrame.this.add();
			}
		});
		mnFile.add(mntmAddContact);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainFrame.this.exit();
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnConfiguration = new JMenu("Configuration");
		menuBar.add(mnConfiguration);
		
		JMenuItem mntmConfigure = new JMenuItem("Configure");
		mnConfiguration.add(mntmConfigure);
		mntmConfigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DialogMediator.getInstance().getCfgDlg().setVisible(true);
			}
		});

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogMediator.getInstance().getSysDlg().setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);
		
		contactsTbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					DialogMediator.getInstance().getTransDlg().clear();
					DialogMediator.getInstance().getTransDlg().addContact(
							DataStore.getInstance().getContact(
									contactsTbl.getSelectedRow()
							)
					);
					DialogMediator.getInstance().getTransDlg().setVisible(true);
				}
			}
		});
	}
	
	private void exit() {
		DataStore.getInstance().save();
		dispose();
	}
	
	public TableModel getTableModel() {
		return contactsTbl.getModel();
	}
	
	public void refreshData() {
		((ContactTableModel) contactsTbl.getModel()).refreshData();
	}
	
	BtnAdd btnAdd;
	BtnEdit btnEdit;
	BtnDelete btnDelete;

	@Override
	public void registerAdd(BtnAdd a) {
		btnAdd = a;
	}

	@Override
	public void registerEdit(BtnEdit e) {
		btnEdit = e;
	}

	@Override
	public void registerDelete(BtnDelete d) {
		btnDelete = d;
	}

	@Override
    public void actionPerformed(ActionEvent ae) {
        Command comd = (Command) ae.getSource();
        comd.execute();
    }

	@Override
	public void add() {
		DialogMediator.getInstance().getContactDlg().clearFields();
		DialogMediator.getInstance().getContactDlg().setVisible(true);
	}

	@Override
	public void edit() {
		int row = contactsTbl.getSelectedRow();
		if (row > -1) {
			DialogMediator.getInstance().getContactDlg().fillFields(row);
			DialogMediator.getInstance().getContactDlg().setVisible(true);
		}
	}

	@Override
	public void delete() {
		ds.removeContact(contactsTbl.getSelectedRow());
		refreshData();
	}
}
