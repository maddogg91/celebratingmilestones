package com.mdsdc.celebratingmilestonesshoppingbusinessservice.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mdsdc.celebratingmilestonesshoppingbusinessservice.models.Cart;
import com.mdsdc.celebratingmilestonesshoppingbusinessservice.models.CartItem;
import com.mdsdc.celebratingmilestonesshoppingbusinessservice.models.NewCartRequest;
import com.mdsdc.celebratingmilestonesshoppingbusinessservice.models.Sequence;
import com.mdsdc.celebratingmilestonesshoppingbusinessservice.models.Shopper;
import com.mdsdc.celebratingmilestonesshoppingbusinessservice.repository.ShoppingRepo;

@RestController
@RequestMapping("/api")
public class ShoppingController {
	
	@Autowired
	ShoppingRepo repo;
	
	@Autowired
	private JavaMailSender emailSender;
	
	private boolean newItem= true;
	
	private Logger logger= Logger.getLogger(ShoppingController.class.getName());
	
	@CrossOrigin
	@PostMapping("/v1/createCart")
	public Cart createCart(@RequestBody String ip) {
		
		if(isNewCart(ip)) {
			List<CartItem> items = new ArrayList<>();
			Cart newCart= new Cart(ip, LocalDateTime.now(), items);
			repo.save(newCart);
			logger.info("Saved");
			return newCart;
		}
		Optional<Cart> userCart= repo.findById(ip);
		logger.info(userCart.get().toString());
		return userCart.get();
		
	}
	@CrossOrigin
	@GetMapping("/v1/sequence")
	public Sequence createSequence() {
	
		int num= getRandomNumberInRange(11111111, 99999999);
		Optional <Cart> sequence= repo.findById(String.valueOf(num));
		while(sequence.isPresent()) {
			num= getRandomNumberInRange(11111111, 99999999);
			sequence= repo.findById(String.valueOf(num));
		}
		Sequence seq= new Sequence();
		seq.setSeq(String.valueOf(num));
		return seq;
	}
	
	private static int getRandomNumberInRange(int min, int max) {
		
		Random r = new Random();
		return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();
		
	}
	
	@CrossOrigin
	@PostMapping("/v1/checkOut")
	public List<CartItem> checkOut(@RequestBody Shopper shopper) throws MessagingException {
		Cart cart= createCart(shopper.getIp());
		 Properties properties = new Properties();
         properties.put("mail.smtp.host", "smtp.gmail.com");
         properties.put("mail.smtp.port", "587");
         properties.put("mail.smtp.auth", "true");
         properties.put("mail.smtp.starttls.enable", "true");
		// creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("ms.celebratingmilestones@gmail.com", "ForTrafficOnly007");
            }
        };
        Session session = Session.getInstance(properties, auth);
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("noreply@celebratingmilestones.com"));
        msg.setSentDate(new Date());
        String header= "<html>";
        
        String customer= shopper.getFirstName() + " " + shopper.getLastName();
        String messageText= header+ ("Caroline,<br><br>The following items were requested by " + shopper.getFirstName() + " " + shopper.getLastName() + 
                " for the through dates listed: " + shopper.getDate() + "<br><br><br>");
        String toMessageText= header+ (shopper.getFirstName() +",<br><br>You have requested the following items for the through dates listed: " + shopper.getDate() +  "<br><br><br>");
        sendQuoteToCustomer(msg, "celebratingmilestonesllc@gmail.com", cart, "Order Request " + customer, messageText);
        msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("noreply@celebratingmilestones.com"));
        msg.setSentDate(new Date());
        sendQuoteToCustomer(msg, shopper.getEmail(), cart, "Celebrating Milestones Quote", toMessageText);
        repo.deleteById(shopper.getIp());
		return cart.getItems();
	}
	
	private void sendQuoteToCustomer(Message msg, String email, Cart cart, 
			String subject, String messageText) throws MessagingException {
		msg.setSubject(subject);
		String foot= "</html>";
        InternetAddress[] toAddresses = { new InternetAddress(email) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
       
        
        String order= "";
        MimeMultipart multiPart= new MimeMultipart();
        BodyPart bodyPart= new MimeBodyPart();
        double grandTotal= 0;
        for(CartItem item : cart.getItems()) {
        	
        	String img= "<img src=" + "\"cid:"+ item.getImg() + "\"/>";
        	order= order + "Item Name: " + item.getItemName() + "<br>Quantity Requested: " + item.getQuantity() + "<br>Subtotal: $" + item.getPrice()*item.getQuantity() +
        			"0<br>Item Description: " + item.getDesc() + "<br>" + img + "<br><br>";
        	grandTotal= grandTotal + (item.getPrice()*item.getQuantity());
        }
        messageText= messageText + order + "Total:$" + grandTotal + "0" + foot;
        bodyPart.setContent(messageText, "text/html");
        multiPart.addBodyPart(bodyPart);
        for(CartItem item : cart.getItems()) {
        	bodyPart= new MimeBodyPart();
        	DataSource fds= new FileDataSource("src/main/resources/"+item.getImg());
        	try {
        		bodyPart.setDataHandler(new DataHandler(fds));
        	}
        	catch (Exception e) {
        		fds= new FileDataSource("src/main/resources/"+"default.jpg");
        	}
        	bodyPart.setHeader("Content-ID", "<" + item.getImg() + ">");
        	multiPart.addBodyPart(bodyPart);
        }
        msg.setContent(multiPart);
        Transport.send(msg);
		
	}
	@CrossOrigin
	@PostMapping("/v1/updateQuantity")
	public void updateQuantity(@RequestBody NewCartRequest request) {
		Optional<Cart> userCart= repo.findById(request.getIp());
		if(userCart.isPresent()) {
			Cart newCart= userCart.get();
			for(CartItem item : newCart.getItems()) {
				if(request.getItem().getItemId() == item.getItemId()) {
					item.setQuantity(request.getItem().getQuantity());
				}
			}
		}
	}
	@CrossOrigin
	@PostMapping("/v1/removeItem")
	public String removeItem(@RequestBody NewCartRequest request) {
		Optional<Cart> userCart= repo.findById(request.getIp());
		if(userCart.isPresent()) {
			Cart newCart= userCart.get();
			for(CartItem item : newCart.getItems()) {
				if(item.getQuantity > 0){
						item.setQuantity(item.getQuantity-1);
					}
					else{
						newCart.getItems().remove(item);
					}
					break;
			}
			repo.save(newCart);
			return "new item removed successfully";
		}
		return "no item removed";
	}
	@CrossOrigin
	@PostMapping("/v1/removeAll")
	public void removeAll(@RequestBody NewCartRequest request) {
		Optional<Cart> userCart= repo.findById(request.getIp());
		if(userCart.isPresent()) {
			repo.deleteById(request.getIp());
			}
	}
	@CrossOrigin
	@PostMapping("/v1/addItem")
	public void addItem(@RequestBody NewCartRequest request) {
		newItem= true;
		if(request.getIp()!= null) {
			if(isNewCart(request.getIp())){
				createCart(request.getIp());
			}
			Optional<Cart> userCart= repo.findById(request.getIp());
			if(userCart.isPresent()) {
				
				Cart newCart= userCart.get();
				
				newCart.getItems().stream().forEach(item->{
					if(item.getItemId()== request.getItem().getItemId()) {
						item.setQuantity(item.getQuantity()+request.getItem().getQuantity());
						repo.save(newCart);
						 newItem=false;
					}
				});
				if(newItem) {
					newCart.getItems().add(request.getItem());
					repo.save(newCart);
				}
			}
		}
	}
	
	public boolean isNewCart(String ip) {
		Optional<Cart> cart= repo.findById(ip);
		if(cart.isPresent()) {
			return false;
		}
		else {
			return true;
		}
	}
}
