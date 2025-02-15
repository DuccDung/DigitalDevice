using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class UserAuthority
{
    public string UserAuthorityId { get; set; } = null!;

    public string? UserId { get; set; }

    public string? AuthorityId { get; set; }

    public string? Description { get; set; }

    public virtual Authority? Authority { get; set; }

    public virtual User? User { get; set; }
}
