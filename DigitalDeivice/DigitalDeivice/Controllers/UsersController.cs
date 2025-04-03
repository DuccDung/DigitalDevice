using DigitalDeivice.Models;
using DigitalDeivice.Models.ViewModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	[Authorize]
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
		//cập nhật thông tin người dùng
		[HttpPut]
		[Route("UpdateUser")]
		public IActionResult UpdateUser(string userId, string Name, string Phone, string Password)
		{
			var user = _digitalDeviceContext.Users.FirstOrDefault(u => u.UserId == userId);
			if (user == null)
			{
				return NotFound("User not found!");
			}

			user.Name = Name;
			user.Phone = Phone;
			user.Password = Password;

			_digitalDeviceContext.SaveChanges();
			return Ok("User updated successfully!");
		}
		[HttpPost]
		[Route("UploadAvatar")]
		public async Task<IActionResult> UploadAvatar(string userId, IFormFile file)
		{
			var user = _digitalDeviceContext.Users.FirstOrDefault(u => u.UserId == userId);
			if (user == null)
			{
				return NotFound("User not found!");
			}

			if (file == null || file.Length == 0)
			{
				return BadRequest("Invalid file!");
			}

			string uploadsFolder = Path.Combine(_webHostEnvironment.WebRootPath, "uploads");
			if (!Directory.Exists(uploadsFolder))
			{
				Directory.CreateDirectory(uploadsFolder);
			}

			string fileName = $"{Guid.NewGuid()}_{file.FileName}";
			string filePath = Path.Combine(uploadsFolder, fileName);

			using (var stream = new FileStream(filePath, FileMode.Create))
			{
				await file.CopyToAsync(stream);
			}

			user.PhotoPath = "/uploads/" + fileName;
			_digitalDeviceContext.SaveChanges();

			return Ok(new { message = "Avatar uploaded successfully!", filePath = user.PhotoPath });
		}
	}

}
