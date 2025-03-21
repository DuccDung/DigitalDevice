using DigitalDeivice.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	public class AuthController : ControllerBase
	{
		private readonly DigitalDeviceContext _context;

		public AuthController(DigitalDeviceContext context)
		{
			_context = context;
		}

		[HttpGet]
		[Route("GetLogin")]
		public IActionResult Login(string Name, string Password)
		{
			var isValidUser = _context.Users.Any(x => x.Name == Name && x.Password == Password);
			if (!isValidUser)
			{
				return Unauthorized();
			}
			var token = GenerateJwtToken(Name);
			var user = _context.Users.FirstOrDefault(x => x.Name == Name && x.Password == Password);
			return Ok(new {user , token });
		}

		private string GenerateJwtToken(string username)
		{
			var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("sjbvsdbvcskjbcvskjvcbsdkjcvbsdkjcvbsdkjcbsdkjc"));
			var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

			var token = new JwtSecurityToken(
				issuer: "duccdung@gmail.com",
				audience: "duccdung@gmail.com",
				expires: DateTime.UtcNow.AddHours(1),
				signingCredentials: creds
			);

			return new JwtSecurityTokenHandler().WriteToken(token);
		}




		[ApiExplorerSettings(IgnoreApi = true)]
		private string GenerateRandomUserId()
		{
			var random = new Random();
			const int idLength = 8; // Độ dài mã ngẫu nhiên sau chữ "U"
			const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

			// Sinh mã ngẫu nhiên
			return "U" + new string(Enumerable.Repeat(chars, idLength)
				.Select(s => s[random.Next(s.Length)]).ToArray());
		}


		[ApiExplorerSettings(IgnoreApi = true)]
		public string GenerateUniqueUserId()
		{
			string userId;

			// Lặp cho đến khi tạo được mã UserId không trùng
			do
			{
				userId = GenerateRandomUserId();
			} while (_context.Users.Any(x => x.UserId == userId));

			return userId;
		}

		[HttpGet]
		[Route("Register")]
		public IActionResult Register(string Name, string Password, string Email, string Phone)
		{
			// Tạo mã UserId ngẫu nhiên và duy nhất
			string UserId = GenerateUniqueUserId();

			User user = new User
			{
				UserId = UserId,
				Name = Name,
				Password = Password,
				Phone = Phone
			};

			var isValid = _context.Users.Any(x => x.Phone == Phone || x.Name == Name && x.Password == Password);
			if (isValid)
			{
				return BadRequest("Register User Has Existed! ");
			}

			_context.Users.Add(user);
			_context.SaveChanges();
			return Ok("Register Success!");
		}
	}
}
