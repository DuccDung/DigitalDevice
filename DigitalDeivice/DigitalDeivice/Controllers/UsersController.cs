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
	//[Authorize]
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
		[Route("GetUser")]
		public IActionResult GetUser(string UserID)
		{
			var User = _digitalDeviceContext.Users.Where(e => e.UserId == UserID).FirstOrDefault();
			if(User == null)
			{
				return NotFound("User not found!");
			}
			return Ok(User);
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

		[HttpGet]
		[Route("SearchUsers")]
		public IActionResult SearchUsers(string keyword)
		{
			try
			{
				var users = _digitalDeviceContext.Users
					.Where(u => u.UserId.Contains(keyword) || u.Name.Contains(keyword))
					.Select(u => new User
					{
						UserId = u.UserId,
						Name = u.Name,
						PhotoPath = u.PhotoPath
					}).ToList();

				return Ok(users);
			}
			catch (Exception ex)
			{
				return StatusCode(500, new { success = false, message = ex.Message });
			}
		}
		[HttpGet]
		[Route("GetUsersByHomeId")]
		public IActionResult GetUsersByHomeID(string homeId)
		{
			var query = from home in _digitalDeviceContext.Homes
						join hu in _digitalDeviceContext.HomeUsers on home.HomeId equals hu.HomeId
						join user in _digitalDeviceContext.Users on hu.UserId equals user.UserId
						where home.HomeId == homeId
						select new User
						{
							UserId = user.UserId,
							Name= user.Name,
							PhotoPath = user.PhotoPath,
						};
			return Ok(query.ToList());
		}
		[HttpDelete]
		[Route("RemoveUserFromHome")]
		public IActionResult RemoveUserFromHome(string userId, string homeId)
		{
			try
			{
				var homeUser = _digitalDeviceContext.HomeUsers
					.FirstOrDefault(hu => hu.UserId == userId && hu.HomeId == homeId);

				if (homeUser == null)
				{
					return NotFound("User is not connected to this home.");
				}

				_digitalDeviceContext.HomeUsers.Remove(homeUser);
				_digitalDeviceContext.SaveChanges();

				return Ok("User has been removed from the home.");
			}
			catch (Exception ex)
			{
				return StatusCode(500, new { success = false, message = ex.Message });
			}
		}

		[HttpPost]
		[Route("AddUserToHome")]
		public IActionResult AddUserToHome(string HomeID , string UserID)
		{
			try
			{
				var home = _digitalDeviceContext.Homes.FirstOrDefault(h => h.HomeId == HomeID);
				if (home == null)
				{
					return NotFound("Home not found");
				}

				var user = _digitalDeviceContext.Users.FirstOrDefault(u => u.UserId == UserID);
				if (user == null)
				{
					return NotFound("User not found");
				}

				var homeUser = new HomeUser
				{
					HomeUserId = HomeID + "_" + UserID + "_" + new Random().Next(1000, 9999),
					HomeId = HomeID,
					UserId = UserID,
					AuthorityID = "Auth_02" 
				};

				_digitalDeviceContext.HomeUsers.Add(homeUser);
				_digitalDeviceContext.SaveChanges();

				return Ok("User added successfully to the home");
			}
			catch (Exception ex)
			{
				return BadRequest($"An error occurred: {ex.Message}");
			}
		}

		

	}

}
