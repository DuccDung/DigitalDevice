using DigitalDeivice.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	[Authorize]
	public class RoomsController : ControllerBase
	{
		private readonly DigitalDeviceContext _digitalDeviceContext;
		public RoomsController(DigitalDeviceContext digitalDeviceContext) {
			_digitalDeviceContext = digitalDeviceContext;
		}
		[HttpGet]
		[Route("GetRoomsByHomeID")]
		public IActionResult GetRooms(string HomeID)
		{
			var rooms = _digitalDeviceContext.Rooms
				.Where(x => x.HomeId == HomeID && !x.RoomId.StartsWith("vehicle_"))
				.ToList();

			return Ok(rooms);
		}

	}
}
