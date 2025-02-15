using DigitalDeivice.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	public class RoomsController : ControllerBase
	{
		private readonly DigitalDeviceContext _digitalDeviceContext;
		public RoomsController(DigitalDeviceContext digitalDeviceContext) {
			_digitalDeviceContext = digitalDeviceContext;
		}
		[HttpGet]
		[Route("GetRoomsByHomeID")]
		public IActionResult GetRooms(String HomeID)
		{
			var rooms = _digitalDeviceContext.Rooms.Where(x => x.HomeId == HomeID).ToList();
			return Ok(rooms);
		}
		
	}
}
