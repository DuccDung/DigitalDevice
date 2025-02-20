namespace DigitalDeivice.Models.ViewModel
{
	public class Home_User
	{
		public string UserId { get; set; } = null!;

		public string Name { get; set; } = null!;

		public string Password { get; set; } = null!;

		public string? Phone { get; set; }

		public string? PhotoPath { get; set; }
		
		
		// ===================================

		public string HomeId { get; set; } = null!;

		public string? Address { get; set; }

		public string? UrlMqtt { get; set; }
		public string? UserMQTT { get; set; }
		public string? PasswordMQTT { get; set; }
	}
}
