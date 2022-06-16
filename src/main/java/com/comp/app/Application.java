package com.comp.app;

import static org.web3j.tx.Transfer.GAS_LIMIT;
import static org.web3j.tx.gas.DefaultGasProvider.GAS_PRICE;

import com.comp.web3j.contracts.Greeting;
import java.io.IOException;
import java.math.BigInteger;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

public class Application {

  private static final String WALLET_LOC = "/usr/local/google/home/iqz/.ethereum/testnet/keystore";
  private static final String WALLET_FILE = "UTC--2022-06-06T21-25-21.640080000Z--d254c36ace38832d7191cfc399c3212069490a7f.json";
  private static final String PROVIDER = "https://eth-goerli.alchemyapi.io/v2/4bTG0A-ASX58kbjFd1bL5-8MgRtEHf0p";
  private static final String PASSWORD = "Zaveri#3125";
  private static final String PUBLIC_ADDRESS = "0xd254c36ace38832d7191cfc399c3212069490a7f";
  private static final String CONTRACT_ADDRESS = "0xb3035d4a277b66e75f732666ed8d037806b7a640";
  private static final String PRIVATE_KEY = "a0a2a8359f4fa881f96d5bdd35718cbe0450881d15dc2056ad50e54a8ea1faa5";

  private Web3j web3j;

  public static void main(String[] args) throws Exception {

    Application app = new Application();
    app.init();
    EthGetBalance ethBalance = app.getEthBalance();
    System.out.println("Eth balance: " + ethBalance.getBalance());
    app.deployAndRunContract();
    ethBalance = app.getEthBalance();
    System.out.println("New Eth balance: " + ethBalance.getBalance());
  }

  public void deployAndRunContract() throws Exception {
    System.out.println("Change 8");
    BigInteger gasprice = BigInteger.valueOf(452893249721253L);

    Credentials credentials =
        Credentials.create(PRIVATE_KEY);

    Greeting contract = Greeting.load(CONTRACT_ADDRESS,
        web3j, credentials,
        new StaticGasProvider(gasprice, GAS_LIMIT));

    String contractAddress = contract.getContractAddress();
    System.out.println("Smart contract deployed to address " + contractAddress);

    System.out.println("Value stored in remote smart contract: " + contract.greet().send());

    TransactionReceipt transactionReceipt = contract.setGreeting("Well hello again").send();

    System.out.println("New value stored in remote smart contract: " + contract.greet().send());
  }

  public void init() {
    web3j =
        Web3j.build(new HttpService(PROVIDER));
  }

  public EthGetBalance getEthBalance() {
    EthGetBalance result = new EthGetBalance();
    try {
      result = web3j.ethGetBalance(PUBLIC_ADDRESS, DefaultBlockParameter.valueOf("latest"))
          .sendAsync().get();
    } catch (Exception ex) {
      System.out.println("Exception thrown!");
      ex.printStackTrace();
    }
    return result;
  }
}
