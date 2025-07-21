import java.math.BigDecimal;

public class TestVNPayAmount {
    public static void main(String[] args) {
        // Test case: 24,999,000 VND
        BigDecimal paymentAmount = new BigDecimal("24999000.00");
        
        System.out.println("=== VNPay Amount Conversion Test ===");
        System.out.println("Original Amount (VND): " + paymentAmount);
        
        // Old way (int) - causes overflow
        try {
            int oldAmount = paymentAmount.multiply(new BigDecimal(100)).intValue();
            System.out.println("Old way (int): " + oldAmount + " (OVERFLOW!)");
        } catch (Exception e) {
            System.out.println("Old way (int): ERROR - " + e.getMessage());
        }
        
        // New way (long) - correct
        long newAmount = paymentAmount.multiply(new BigDecimal(100)).longValue();
        System.out.println("New way (long): " + newAmount + " (CORRECT!)");
        
        // Verify conversion back
        BigDecimal convertedBack = new BigDecimal(newAmount).divide(new BigDecimal(100));
        System.out.println("Converted back: " + convertedBack + " VND");
        
        System.out.println("=====================================");
        
        // Test with max int value
        System.out.println("\n=== Integer Limits Test ===");
        System.out.println("Max int value: " + Integer.MAX_VALUE);
        System.out.println("24,999,000 * 100 = " + (24999000L * 100));
        System.out.println("Exceeds int limit: " + ((24999000L * 100) > Integer.MAX_VALUE));
        System.out.println("============================");
    }
}
