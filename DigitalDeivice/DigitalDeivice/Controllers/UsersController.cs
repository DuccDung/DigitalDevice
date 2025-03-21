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
	}

}
