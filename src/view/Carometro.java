package view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.DAO;
import utils.Validador;

public class Carometro extends JFrame {

	// instanciar objetos JDBC

	// Classe que será responsavel pela conexao
	DAO dao = new DAO();
	private Connection con;

	// Para fazer a persistencia no banco de dados usaremos a classe
	// preparedStatement pst. Esse por sua vez e responsavel por preparar e executar
	// a instrução sql (JDBC)
	private PreparedStatement pst;

	// Será responsavel por trazer os dados do banco de dados (JDBC)
	private ResultSet rs;

	// Criação de um objeto que será responsavel pelo fluxo de bytes de uma imagem
	private FileInputStream fis;

	// Variavel global para armazenar tamanho da imagem (bytes)
	private int tamanho;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblStatus;
	private JLabel lblData;
	private JLabel lblNewLabel;
	private JTextField txtRa;
	private JTextField txtNome;
	private JLabel lblFoto;
	private JButton btnReset;
	private JButton btnBuscar;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Carometro frame = new Carometro();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Carometro() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				status();
				setarData();
			}
		});
		setTitle("Carômetro");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Carometro.class.getResource("/img/instagram.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.textHighlight);
		panel.setBounds(0, 278, 626, 55);
		contentPane.add(panel);
		panel.setLayout(null);

		lblStatus = new JLabel("");
		lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dboff.png")));
		lblStatus.setBounds(570, 10, 32, 32);
		panel.add(lblStatus);

		lblData = new JLabel("");
		lblData.setForeground(SystemColor.text);
		lblData.setFont(new Font("Arial", Font.PLAIN, 14));
		lblData.setBounds(22, 10, 273, 32);
		panel.add(lblData);

		lblNewLabel = new JLabel("RA");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 13));
		lblNewLabel.setBounds(25, 36, 45, 13);
		contentPane.add(lblNewLabel);

		txtRa = new JTextField();
		txtRa.setBounds(54, 33, 96, 19);
		contentPane.add(txtRa);
		txtRa.setColumns(10);

		// Uso do PlainDocument para limitar os campos
		txtRa.setDocument(new Validador(6));

		JLabel lbltexto = new JLabel("Nome");
		lbltexto.setFont(new Font("Arial", Font.PLAIN, 13));
		lbltexto.setBounds(25, 73, 45, 13);
		contentPane.add(lbltexto);

		txtNome = new JTextField();
		txtNome.setBounds(64, 70, 244, 19);
		contentPane.add(txtNome);
		txtNome.setColumns(10);

		// Uso do PlainDocument paralimitar os campos
		txtNome.setDocument(new Validador(30));

		lblFoto = new JLabel("");
		lblFoto.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/photo.png")));
		lblFoto.setBounds(360, 10, 256, 256);
		contentPane.add(lblFoto);

		JButton btnCarregar = new JButton("Carregar Foto");
		btnCarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carregarFoto();

			}
		});
		btnCarregar.setForeground(SystemColor.textHighlight);
		btnCarregar.setBounds(184, 121, 124, 21);
		contentPane.add(btnCarregar);

		JButton btnAdicionar = new JButton("");
		btnAdicionar.setToolTipText("Adicionar");
		btnAdicionar.setIcon(new ImageIcon(Carometro.class.getResource("/img/create.png")));
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				adicionar();
			}
		});
		btnAdicionar.setBounds(25, 200, 64, 64);
		contentPane.add(btnAdicionar);

		btnReset = new JButton("");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				reset();
			}
		});
		btnReset.setIcon(new ImageIcon(Carometro.class.getResource("/img/eraser.png")));
		btnReset.setToolTipText("Limpar os campos");
		btnReset.setBounds(266, 200, 64, 64);
		contentPane.add(btnReset);

		btnBuscar = new JButton("Buscar");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				buscarRa();
			}
		});
		btnBuscar.setForeground(SystemColor.textHighlight);
		btnBuscar.setBounds(184, 32, 90, 21);
		contentPane.add(btnBuscar);

	}

	// Metodo que ira testar a conecção com o banco de dados
	private void status() {
		try {
			con = dao.conectar();
			if (con == null) {
				// System.out.println("Erro de conecção");
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dboff.png")));

			} else {
				// System.out.println("Banco de dados conectado");

				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dbon.png")));
			}
			con.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void setarData() {
		// Tras a data atual
		Date data = new Date();
		// Resposnsavel pela formatação da data
		DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL);
		// Modifica o texto do JLabel pela data atual formatada pelo formatador
		lblData.setText(formatador.format(data));

	}

	private void carregarFoto() {

		// Classe modelo que serve para gerar um seletor proprio de arquivos. Semelhante
		// ao windows explorer
		JFileChooser jfc = new JFileChooser();

		// Titulo da caixa que ira aparecer
		jfc.setDialogTitle("Selecionar arquivo");

		// Filtro
		jfc.setFileFilter(
				new FileNameExtensionFilter("Arquivo de imagens (*.PNG, *.JPG,*.JPEG)", "png", "jpg", "jpeg"));

		// O this se refere à instância atual da classe Carometro, que é a janela
		// principal do
		// aplicativo. Ao passar this como argumento, eu estou dizendo ao JFileChooser
		// para exibir a caixa de diálogo em relação a essa janela
		int resultado = jfc.showOpenDialog(this);

		// Se o usuario selecionou a imagem eu quero pega-la e substituir a imagem da
		// camera pela imagem carregada
		if (resultado == JFileChooser.APPROVE_OPTION) {
			try {
				fis = new FileInputStream(jfc.getSelectedFile());
				tamanho = (int) jfc.getSelectedFile().length();

				// Faz a leitura da imagem atravez do objeto JFC e redimenciona essa imagem para
				// caber na label com o nome de lblFoto. O ultimo parametro( SCALE_ SMOOTH) usa
				// a
				// melhor resolução possivel
				Image foto = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(lblFoto.getWidth(),
						lblFoto.getHeight(), Image.SCALE_SMOOTH);
				lblFoto.setIcon(new ImageIcon(foto));
				lblFoto.updateUI();

			} catch (Exception e) {
				System.out.println(e);
			}
		}

	}

	private void adicionar() {

		if (txtNome.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Preencha o nome!!");

			// Agora colocaremos o cursor do mouse nessa caixa de texto
			txtNome.requestFocus();
			reset();

		} else {
			// OS conteudos das aspas serão substituidas pelos values do nome e da foto
			// presente mo formulario
			String insert = "insert into alunos(nome,foto) values(?,?)";

			try {
				con = dao.conectar();
				pst = con.prepareStatement(insert);

				// substituimos o 1 campo de ? na variavel insert pelo que foi digitado no campo
				// nome (txtNome)
				pst.setString(1, txtNome.getText());

				// 2 está relacionado a segunda interrogação, fis está relacionado ao arquivo da
				// imagem, tamanho está relacionado ao tamanho da imagem :v
				pst.setBlob(2, fis, tamanho);

				// Comando que grava o registro do aluno no banco de dados mysql. Se der certo o
				// resultado é 1, se errado o resultado é 0
				int confirma = pst.executeUpdate();

				if (confirma == 1) {

					JOptionPane.showMessageDialog(null, "Aluno cadastrado com Sucesso");
				} else {
					JOptionPane.showMessageDialog(null, "Aluno Não cadastrado :(");
				}

				con.close();

			} catch (Exception e) {
				System.out.println(e);
			}

		}

	} // Fim do metodo adicionar

	public void buscarRa() {

		// Se você for buscar o RA e não preencher o numero será feito essa validação
		// abaixo
		if (txtRa.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, " Digite o RA: ");
			txtRa.requestFocus();
		} else {
			String readRA = "select * from alunos where ra = ?";

			try {
				// Abrimos uma conecção com o banco de dados
				con = dao.conectar();
				// Preparamos a instrução sql
				pst = con.prepareStatement(readRA);

				// modificamos o valor do ? pelo numero que foi digitado no RA
				pst.setString(1, txtRa.getText());
				// Usaremos o rs para exibição do resultado, onde executeQuery irá executar o
				// comando presente na strign readRA. O resultado é armazenado em rs
				rs = pst.executeQuery();

				// O metodo next busca um registro correspondente ao select no banco de dados.
				// Se existir ele ira trazer
				if (rs.next()) {

					// Aqui puxamos o registro 2 da tabela de alunos. A coluna nome. Ele modifica o
					// conteudo de txtNome pelo registro no banco de dados
					txtNome.setText(rs.getString(2));

					// Para trazer a imagem façamos o seguinte
					// A classe Blob cria um objeto blob e em seguida recebe os dados da imagem.
					// Fazemos o cast, pois os dados recebidos estão em binario
					Blob blob = (Blob) rs.getBlob(3);

					// Aqui recebemos o arquivo em formato binario e depois criamos um vetor que
					// pega os dados em formato binario e converte para
					// uma imagem
					byte[] img = blob.getBytes(1, (int) blob.length());

					// Trata-se de um "papel" onde os bytes podem ser inseridos e seram entendidos
					BufferedImage imagem = null;

					try {
						// Armazenamos os codigos em bytes dentro de imagem, onde está consegue entender
						// e transformar em uma imagem
						imagem = ImageIO.read(new ByteArrayInputStream(img));

					} catch (Exception e) {
						System.out.println(e);
					}

					// colocamos a imagem em uma variavel com seu tipo seu icone( A JLabel aceita
					// imagem ou icone )
					ImageIcon icone = new ImageIcon(imagem);

					// Ajusta a resolução
					Icon foto = new ImageIcon(icone.getImage().getScaledInstance(lblFoto.getWidth(),
							lblFoto.getHeight(), Image.SCALE_SMOOTH));

					lblFoto.setIcon(foto);

				} else {

					JOptionPane.showMessageDialog(null, "Aluno não Cadastrado");
				}
				con.close();
			} catch (Exception e) {
				System.out.println(e);
			}

		}

	}

	private void reset() {

		txtRa.setText(null);
		txtNome.setText(null);
		// Modificamos a foto pela imagem da camera
		lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/photo.png")));
		txtNome.requestFocus();
	}

}
