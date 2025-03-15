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
	}
}
