package com.daesangit.xamarin.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Authorization {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey; 
    private String token;
    
    public String getToken() {
        return this.token;
    }
    
    private static Authorization instance = new Authorization();
	// 생성자
    private Authorization () {
        System.out.println( "call Authorization constructor." );
        
        //keyGenerator 대칭키 사용
        KeyPairGenerator keyPairGenerator = null; //비대칭키 (공개, 개인)
        try {
            //SecureRandom secureRandom = new SecureRandom();
            //secureRandom.setSeed(new byte[20]);
            //secureRandom.setSeed(8);
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            //keyPairGenerator.initialize(2048, secureRandom);
            keyPairGenerator.initialize(1024);
                  
            KeyPair kp = keyPairGenerator.genKeyPair();
            if(this.publicKey == null)
            {
                this.publicKey = (RSAPublicKey)kp.getPublic();
            }
            
            if(this.privateKey == null)
            {
                this.privateKey = (RSAPrivateKey)kp.getPrivate();            
            }
        } catch(NoSuchAlgorithmException ex) {
        	System.out.println(ex.getMessage());
        }            
        
        System.out.println("publicKey : " + publicKey);        
        System.out.println("privateKey : " + privateKey);        

        LocalDateTime EXPIRES_LOCAL = LocalDateTime.now();  
        Date EXPIRES_DATE  = new Date();
        
        //인증토큰 유효기간 설정
        Calendar c = Calendar.getInstance(); 
        Date now = c.getTime();
        c.setTime(EXPIRES_DATE); 
        c.add(Calendar.HOUR, 24);
        EXPIRES_DATE = c.getTime();    
        
        String token =" ";
        try
        {
	        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
	        token = JWT.create()
	        .withIssuer("bcwms")
	        .withJWTId("bcwms") //JWT ID로 토큰에 대한 식별자이다.
	        .withAudience("bcwms") //이 토큰을 사용할 수신자(Audience)
	        .withExpiresAt(EXPIRES_DATE) //만료시간(Expiration Time)은 만료시간이 지난 토큰은 거절해야 한다.
	        .withIssuedAt(now) //토큰이 발급된 시간(Issued At)
	        .withNotBefore(now) //Not Before의 의미로 이 시간 이전에는 토큰을 처리하지 않아야 함을 의미한다.
	        .withClaim("SCOPE", "INBOUND|OUTBOUND|PUTAWAY|INVENTORY")
	        .withClaim("SCOPE2", "INBOUND|OUTBOUND|PUTAWAY")       
	        .sign(algorithm); 
        }catch(Exception ex) {
        	System.out.println(ex.getMessage());
        }
        
        this.token = token;
    }
	// 조회 method
    public static Authorization getInstance () {
    	return instance;
    }
	
    public void print () {
    	System.out.println("It's print() method in EagerInitialization instance.");
        System.out.println("instance hashCode > " + instance.hashCode());
    } 
    
    public boolean VerifyToken(String token) 
    {
        boolean result = false;
        
        try {
            
            if(this.publicKey == null || this.privateKey == null || token == null || token.isEmpty())
            {
               System.out.println("-----다시 로그인 해주세요.-----");
               return false;
            }
            /*
            java.security.KeyPairGenerator keyGenerator = null;
            java.security.interfaces.RSAPublicKey publicKey = null;
            java.security.interfaces.RSAPrivateKey privateKey = null;                    
            
            keyGenerator = KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(1024);

            KeyPair kp = keyGenerator.genKeyPair();
            publicKey = (RSAPublicKey)kp.getPublic();
            privateKey = (RSAPrivateKey)kp.getPrivate();            
            */
            Algorithm algorithm = Algorithm.RSA256(this.publicKey, this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("bcwms")
                //.acceptLeeway(1) 
                //.acceptExpiresAt(5) // 생성한 만료일 기준 5초 연장
                .build(); //Reusable verifier instance                   
            /*
            Algorithm algorithm = Algorithm.HMAC256("bcwms");
            JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("bcwms")
                .acceptLeeway(1) 
                .acceptExpiresAt(5) // 생성한 만료일 기준 5초 연장
                .build(); //Reusable verifier instance
            */
            
            DecodedJWT jwt = verifier.verify(token);

            Claim  claim = jwt.getClaim("SCOPE");
            System.out.println("jwt claims SCOPE       : " +   claim.asString());

            Claim  claim2 = jwt.getClaim("SCOPE2");
            System.out.println("jwt claims SCOPE2       : " +   claim2.asString());            

            System.out.println("=================== test_verifyJwtToken ===================");
            System.out.println("jwt token         : " + jwt.getToken());
            System.out.println("jwt algorithm     : " + jwt.getAlgorithm());
            System.out.println("jwt claims        : " + jwt.getClaims());
            System.out.println("jwt issuer        : " + jwt.getIssuer());
            System.out.println("jwt issuer date   : " + jwt.getIssuedAt());
            System.out.println("jwt expires date  : " + jwt.getExpiresAt());
            System.out.println("jwt signature     : " + jwt.getSignature());
            System.out.println("jwt type          : " + jwt.getType());
            System.out.println("jwt key id        : " + jwt.getKeyId());
            System.out.println("jwt id            : " + jwt.getId());
            System.out.println("jwt subject       : " + jwt.getSubject());
            System.out.println("jwt content type  : " + jwt.getContentType());
            System.out.println("jwt audience list : " + jwt.getAudience());

            result = true;
        } catch (JWTVerificationException exception){
            //UTF-8 encoding not supported
        }
        //Invalid signature/claims
        
        return result;
    }    
}