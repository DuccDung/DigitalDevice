using DigitalDeivice.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace DigitalDeivice.Controllers
{
	[Route("api/[controller]")]
	[ApiController]
	public class AdminController : ControllerBase
	{
		private readonly DigitalDeviceContext _context;

		public AdminController(DigitalDeviceContext context)
		{
			_context = context;
		}

		// GET: api/Admin/Homes (Retrieve all homes)
		[HttpGet("Homes")]
		public async Task<ActionResult<IEnumerable<Home>>> GetHomes()
		{
			return await _context.Homes.ToListAsync();
		}

		// by dongtruong
		[HttpGet]
		[Route("RoomCount")]
		public async Task<ActionResult<int>> GetRoomCount(string homeId)
		{
			if (string.IsNullOrEmpty(homeId))
			{
				return BadRequest("HomeId không được để trống");
			}

			var count = await _context.Rooms
				.Where(r => r.HomeId == homeId && r.RoomId.StartsWith("r_"))
				.CountAsync();

			return Ok(count);
		}


		// GET: api/Admin/Homes/5 (Retrieve a home by ID)
		[HttpGet("Homes/{id}")]
		public async Task<ActionResult<Home>> GetHome(string id)
		{
			var home = await _context.Homes.FindAsync(id);
			if (home == null)
			{
				return NotFound();
			}
			return home;
		}

		[HttpPost("Homes")]
		public async Task<ActionResult<Home>> PostHome(
		[FromQuery] string homeId,
		[FromQuery] string name,
		[FromQuery] string? address = null,
		[FromQuery] string? photoPath = null,
		[FromQuery] string? urlMqtt = null,
		[FromQuery] string? userMQTT = null,
		[FromQuery] string? passwordMQTT = null)
		{
			var home = new Home
			{
				HomeId = homeId,
				Name = name,
				Address = address,
				PhotoPath = photoPath,
				UrlMqtt = urlMqtt,
				UserMQTT = userMQTT,
				PasswordMQTT = passwordMQTT
			};

			_context.Homes.Add(home);
			await _context.SaveChangesAsync();

			return CreatedAtAction(nameof(GetHome), new { id = home.HomeId }, home);
		}
		// update home
		[HttpPut("Homes/{id}")]
		public async Task<IActionResult> PutHome(
		string id,
		[FromQuery] string name,
		[FromQuery] string? address = null,
		[FromQuery] string? photoPath = null,
		[FromQuery] string? urlMqtt = null,
		[FromQuery] string? userMQTT = null,
		[FromQuery] string? passwordMQTT = null)
		{
			var home = await _context.Homes.FindAsync(id);

			if (home == null)
			{
				return NotFound();
			}

			// Update the existing home record with new values
			home.Name = name;
			home.Address = address;
			home.PhotoPath = photoPath;
			home.UrlMqtt = urlMqtt;
			home.UserMQTT = userMQTT;
			home.PasswordMQTT = passwordMQTT;

			try
			{
				await _context.SaveChangesAsync();
			}
			catch (DbUpdateConcurrencyException)
			{
				if (!HomeExists(id))
				{
					return NotFound();
				}
				else
				{
					throw;
				}
			}

			return NoContent(); // HTTP 204 - No Content (successful update)
		}


		// DELETE: api/Admin/Homes/5 (Delete a home)
		[HttpDelete("Homes/{id}")]
		public async Task<IActionResult> DeleteHome(string id)
		{
			var home = await _context.Homes.FindAsync(id);
			if (home == null)
			{
				return NotFound();
			}

			_context.Homes.Remove(home);
			await _context.SaveChangesAsync();

			return NoContent();
		}

		private bool HomeExists(string id)
		{
			return _context.Homes.Any(e => e.HomeId == id);
		}
	}
}
