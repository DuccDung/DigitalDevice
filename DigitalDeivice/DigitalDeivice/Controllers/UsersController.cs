using DigitalDeivice.Models;
using DigitalDeivice.Models.ViewModel;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	public class UsersController : ControllerBase
	{
		private readonly DigitalDeviceContext _digitalDeviceContext;
		public readonly IWebHostEnvironment _webHostEnvironment;

		public UsersController(DigitalDeviceContext digitalDeviceContext, IWebHostEnvironment webHostEnvironment)
		{
			_digitalDeviceContext = digitalDeviceContext;
			_webHostEnvironment = webHostEnvironment;
		}

		[HttpGet]
		[Route("GetAuth")]
		public IActionResult GetAdmins(string HomeID, string UserID) // check quyền hạn của user trong căn nhà hiện tại
		{
			var query = from hu in _digitalDeviceContext.HomeUsers
						join u in _digitalDeviceContext.Users on hu.UserId equals u.UserId
						join h in _digitalDeviceContext.Homes on hu.HomeId equals h.HomeId
						join a in _digitalDeviceContext.Authorities on hu.AuthorityID equals a.AuthorityId
						where u.UserId == UserID && h.HomeId == HomeID
						select new
						{
							AuthorityId = a.AuthorityId,
							AuthorityName = a.NameAuthority
						};
			return Ok(query.FirstOrDefault());
		}

		[HttpGet]
		[Route("GetUsers")]
		public IActionResult GetUsers()
		{
			var Users = _digitalDeviceContext.Users.ToList();
			return Ok(Users);
		}

		[HttpGet]
		[Route("GetLogin")]
		public IActionResult Login(string Name, string Password)
		{
			var isValidUser = _digitalDeviceContext.Users.Any(x => x.Name == Name && x.Password == Password);
			if (!isValidUser)
			{
				return Unauthorized(new { data = false, mesage = "Invalid login" });
			}
			var user = _digitalDeviceContext.Users.FirstOrDefault(x => x.Name == Name && x.Password == Password);
			return Ok(user);
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
			} while (_digitalDeviceContext.Users.Any(x => x.UserId == userId));

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

			var isValid = _digitalDeviceContext.Users.Any(x => x.Phone == Phone || x.Name ==Name && x.Password == Password);
			if (isValid) {
				return BadRequest("Register User Has Existed! ");
			}

			_digitalDeviceContext.Users.Add(user);
			_digitalDeviceContext.SaveChanges();
			return Ok("Register Success!");
		}

		[HttpGet]
		[Route("GetHomeUsersByUserId")]

		public List<Home_User> GetHomeUsersByUserId(string userId)
		{
			var query = from hu in _digitalDeviceContext.HomeUsers
							join u in _digitalDeviceContext.Users on hu.UserId equals u.UserId
							join h in _digitalDeviceContext.Homes on hu.HomeId equals h.HomeId
							where u.UserId == userId
							select new Home_User
							{
								// Home properties
								HomeId = h.HomeId,
								Address = h.Address,
								UrlMqtt = h.UrlMqtt,
								UserMQTT = h.UserMQTT,
								PasswordMQTT = h.PasswordMQTT,

								// User properties
								UserId = u.UserId,
								Name = h.Name,
								Password = u.Password,
								Phone = u.Phone,
								PhotoPath = u.PhotoPath
							};

				return query.ToList();
		}
	}

}
